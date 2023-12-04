package in.codifi.api.service;

import java.io.File;
import java.net.URLConnection;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.entity.DocumentEntity;
import in.codifi.api.entity.EmailLogEntity;
import in.codifi.api.entity.EmailTemplateEntity;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.repository.DocumentEntityRepository;
import in.codifi.api.repository.EmailLogRepository;
import in.codifi.api.repository.EmailTemplateRepository;
import in.codifi.api.service.spec.IKraDocDownloadService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;

@ApplicationScoped
public class KraDocDownloadService implements IKraDocDownloadService {

	private static String OS = System.getProperty("os.name").toLowerCase();

	@Inject
	DocumentEntityRepository docrepository;
	@Inject
	ApplicationUserRepository applicationUserRepository;
	@Inject
	ApplicationProperties props;

	@Inject
	CommonMethods commonMethods;
	@Inject
	EmailTemplateRepository emailTemplateRepository;
	@Inject
	Mailer mailer;
	@Inject
	EmailLogRepository emailLogRepository;
	@Inject
	public void MailService(Mailer javaMailSender) {
		this.mailer = javaMailSender;
	}


	@Override
	public Response downloadkraFile(@NotNull long applicationId) {
		String inputFilePath = null;
		String slash = EkycConstants.UBUNTU_FILE_SEPERATOR;
		if (OS.contains(EkycConstants.OS_WINDOWS)) {
			slash = EkycConstants.WINDOWS_FILE_SEPERATOR;
		}
		String outputPath = props.getFileBasePath() + applicationId;
		new File(outputPath).mkdir();
		try {
			Optional<ApplicationUserEntity> userEntity = applicationUserRepository.findById(applicationId);
			if (userEntity.isPresent() && userEntity.get().getSmsVerified() == 1
					&& userEntity.get().getEmailVerified() == 1 && userEntity.get().getEsignCompleted() == 1) {
				DocumentEntity documents = docrepository.findByApplicationIdAndDocumentType(applicationId,
						EkycConstants.DOC_ESIGN);
				if (documents != null) {
					inputFilePath = documents.getAttachementUrl();
					try (PDDocument document = PDDocument.load(new File(inputFilePath))) {
						PDPageTree pages = document.getDocumentCatalog().getPages();
						int pageIndex = 2;
						int pageIndex1=38;
						if (pageIndex < pages.getCount()) {
							PDPage thirdPage = pages.get(pageIndex);
							PDDocument newDocument = new PDDocument();
							newDocument.addPage(thirdPage);
							PDPage Page1 = pages.get(pageIndex1);
							newDocument.addPage(Page1);

							int startPageIndex =pages.getCount()-2;
							for (int i = startPageIndex; i < pages.getCount(); i++) {
								newDocument.addPage(pages.get(i));
							}
							String outputFilePath = props.getFileBasePath() + applicationId;
							//new File(outputPath).mkdir();
							String fileName = userEntity.get().getPanNumber() + "_" + EkycConstants.DOC_KRA
									+ EkycConstants.PDF_EXTENSION;
							newDocument.save(outputFilePath + slash + fileName);
							//System.out.println("Third page saved at: " + outputFilePath + slash + fileName);						
							newDocument.close();
							saveEsignSplitDocumntDetails(applicationId, outputFilePath + slash + fileName, fileName);
							sendKraDocMail(outputFilePath + slash + fileName,fileName);
							String contentType = URLConnection.guessContentTypeFromName(fileName);
							String path= outputFilePath + slash + fileName;
							File savedFile = new File(path);
							ResponseBuilder response = Response.ok((Object) savedFile);
							response.type(contentType);
							response.header("Content-Disposition", "attachment;filename=" + savedFile.getName());
							return response.build();
						} else {
							return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(MessageConstants.FILE_NOT_FOUND).build();
						}
					}

				} else {
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(MessageConstants.FILE_NOT_FOUND).build();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(MessageConstants.FILE_NOT_FOUND).build();
	}

	public void saveEsignSplitDocumntDetails(long applicationId, String documentPath, String fileName) {
		DocumentEntity oldEntity = docrepository.findByApplicationIdAndDocumentType(applicationId,
				EkycConstants.DOC_KRA);
		if (oldEntity == null) {
			DocumentEntity documentEntity = new DocumentEntity();
			documentEntity.setApplicationId(applicationId);
			documentEntity.setAttachementUrl(documentPath);
			documentEntity.setAttachement(fileName);
			documentEntity.setDocumentType(EkycConstants.DOC_KRA);
			documentEntity.setTypeOfProof(EkycConstants.DOC_KRA);
			docrepository.save(documentEntity);
		} else {
			oldEntity.setAttachementUrl(documentPath);
			oldEntity.setAttachement(fileName);
			docrepository.save(oldEntity);
		}
	}

	public void sendKraDocMail( String filePath, String fileName) {
		EmailTemplateEntity emailTemplateEntity = emailTemplateRepository.findByKeyData("kra");
		if (emailTemplateEntity != null && emailTemplateEntity.getBody() != null
				&& emailTemplateEntity.getSubject() != null) {
			String bodyMessage = emailTemplateEntity.getBody();
			//String body = bodyMessage.replace("{UserName}", name);
			String subject = emailTemplateEntity.getSubject();
			Mail mail = Mail.withHtml(emailTemplateEntity.getToAddress(), subject, bodyMessage);
			File f = new File(filePath);
			String contentType = URLConnection.guessContentTypeFromName(fileName);
			mail.addAttachment(fileName, f, contentType);
			mailer.send(mail);
			storeEmailLog(bodyMessage, subject, "The email was sent: " + mail, "sendEsignedMail", emailTemplateEntity.getToAddress());
		}
	}
	@Transactional
	public void storeEmailLog(String message, String reqSub, String emailResponse, String logMethod, String mailId) {
	    // Check for null values and throw an IllegalArgumentException if any are null
	    if (message == null || reqSub == null || emailResponse == null || logMethod == null || mailId == null) {
	        throw new IllegalArgumentException("Request, ReqSub, EmailResponse, logMethod, or mailId cannot be null.");
	    }

	    try {
	        // Create a new EmailLogEntity instance
	        EmailLogEntity emailLogEntity = new EmailLogEntity();
	        emailLogEntity.setEmailId(mailId);
	        emailLogEntity.setLogMethod(logMethod);
	        emailLogEntity.setReqLogSub(reqSub);
	        emailLogEntity.setReqLog(message);
	        emailLogEntity.setResponseLog(emailResponse);

	        // Save the EmailLogEntity to the database
	        emailLogRepository.save(emailLogEntity); // Assuming "emailLogRepository" supports "persist"

	        // Optionally, log a success message
	        System.out.println("Email log saved successfully.");
	    } catch (Exception e) {
	        // Handle the exception appropriately, e.g., log it or rethrow it
	        e.printStackTrace();
	    }
	}
}
