package in.codifi.api.utilities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mail.MessagingException;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.ApiStatusEntity;
import in.codifi.api.entity.EmailLogEntity;
import in.codifi.api.entity.EmailTemplateEntity;
import in.codifi.api.entity.ErrorLogEntity;
import in.codifi.api.entity.SmsLogEntity;
import in.codifi.api.repository.ApiStatusRepository;
import in.codifi.api.repository.EmailLogRepository;
import in.codifi.api.repository.EmailTemplateRepository;
import in.codifi.api.repository.ErrorLogRepository;
import in.codifi.api.repository.KraKeyValueRepository;
import in.codifi.api.repository.SmsLogRepository;
import in.codifi.api.request.model.BankAddressModel;
import in.codifi.api.response.model.ResponseModel;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;

@ApplicationScoped
public class CommonMethods {

	@Inject
	EmailTemplateRepository emailTemplateRepository;
	@Inject
	SmsLogRepository smsLogRepository;
	@Inject
	Mailer mailer;
	@Inject
	CommonMail commonMail;
	@Inject
	ApplicationProperties props;
	@Inject
	ApiStatusRepository apiStatusRepository;
	@Inject
	KraKeyValueRepository kraKeyValueRepository;
	@Inject
	ErrorLogRepository errorLogRepository;
	@Inject
	EmailLogRepository emailLogRepository;

	public ResponseModel constructFailedMsg(String failesMessage) {
		ResponseModel model = new ResponseModel();
		model.setStat(MessageConstants.FAILED_STATUS);
		model.setMessage(MessageConstants.FAILED_MSG);
		model.setReason(failesMessage);
		return model;
	}

	public void SaveLog(Long applicationId, String className, String methodName, String reason) {
		ErrorLogEntity errorLogEntity = errorLogRepository.findByApplicationIdAndClassNameAndMethodName(applicationId,
				className, methodName);
		if (errorLogEntity == null) {
			errorLogEntity = new ErrorLogEntity();
			errorLogEntity.setApplicationId(applicationId);
			errorLogEntity.setClassName(className);
			errorLogEntity.setMethodName(methodName);
		}
		errorLogEntity.setReason(reason);
		if (errorLogEntity != null) {
			errorLogRepository.save(errorLogEntity);
		}
	}

	@Inject
	public void MailService(Mailer javaMailSender) {
		this.mailer = javaMailSender;
	}

	public void sendErrorMail(String errorMessage, String errorCode) {
		EmailTemplateEntity emailTemplateEntity = emailTemplateRepository.findByKeyData("error");
		if (emailTemplateEntity != null && emailTemplateEntity.getBody() != null
				&& emailTemplateEntity.getSubject() != null && emailTemplateEntity.getToAddress() != null) {
			String bodyMessage = emailTemplateEntity.getBody();
			String body = bodyMessage.replace("{errorMessage}", errorMessage).replace("{errorCode}", errorCode);
			String subject = emailTemplateEntity.getSubject();
			Mail mail = Mail.withHtml(emailTemplateEntity.getToAddress(), subject, body);

			if (emailTemplateEntity.getCc() != null) {
				String[] ccAddresses = emailTemplateEntity.getCc().split(",");
				for (String ccAddress : ccAddresses) {
					mail = mail.addCc(ccAddress.trim());
				}
			}
			mailer.send(mail);
			System.out.println("The email was sent in error message: " + mail);
		}
	}

	/**
	 * Method to find bank address by ifsc
	 * 
	 * @author Vennila
	 * @param ifscCode
	 * @return
	 */
	public BankAddressModel findBankAddressByIfsc(String ifscCode) {
		BankAddressModel model = null;
		try {
			URL url = new URL(props.getRazorpayIfscUrl() + ifscCode);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			if (conn.getResponseCode() != 200) {
				return model;
			}
			BufferedReader br1 = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			while ((output = br1.readLine()) != null) {
				ObjectMapper om = new ObjectMapper();
				model = om.readValue(output, BankAddressModel.class);
			}
		} catch (Exception e) {
			return model;
		}
		return model;
	}

	/**
	 * Method to send rejection mail to email
	 * 
	 */
	public void sendRejectionMail(String userName, String emailId, long applicationId) throws MessagingException {
		EmailTemplateEntity emailTempentity = emailTemplateRepository.findByKeyData("Rejection");
		String body_Message = emailTempentity.getBody();
		String body = body_Message.replace("{UserName}", userName);

		String subject = emailTempentity.getSubject();
		List<ApiStatusEntity> apiStatusEntities = apiStatusRepository.findByApplicationIdAndStatus(applicationId, 0);
		if (apiStatusEntities != null && !apiStatusEntities.isEmpty()) {
			// Create a StringBuilder to build the formatted stage and reason
			StringBuilder formattedStagesWithReasons = new StringBuilder();
			for (ApiStatusEntity apiStatusEntity : apiStatusEntities) {
				String stageFromKra = kraKeyValueRepository.getkeyValueForKra("12", "STAGE_REJECTION",
						apiStatusEntity.getStage());
				String setReason = apiStatusEntity.getReason();

				// Append each stage and reason to the formattedStagesWithReasons StringBuilder
				formattedStagesWithReasons.append("<div class=\"stage\"><b>").append(stageFromKra)
						.append(":</b><br>&emsp;&emsp;").append(setReason).append("<br></div>");
			}

			// Convert the StringBuilder to a string
			String stagesWithReasons = formattedStagesWithReasons.toString();

			body = body.replace("{StagesWithReasons}", stagesWithReasons);
			body = body.replace("{ReSubmitLink}", "https://ekyc.gopocket.in");
			List<String> toAdd = Collections.singletonList(emailId);
			List<String> bcc = Collections.singletonList("ekycsupport@gopocket.in");
			commonMail.sendMail(toAdd, subject, body,bcc);
		}
	}

	/**
	 * Method to create smsLogMethod
	 * 
	 * @author Vennila
	 * @param EmailLogEntity
	 * @return
	 */

	public void storeEmailLog(String message, String ReqSub, String emailResponse, String logMethod,
			List<String> mailIds) {
		if (message == null || emailResponse == null || logMethod == null) {
			throw new IllegalArgumentException("Request, EmailResponse, or logMethod cannot be null.");
		}

		try {
			for (String mailId : mailIds) {
				EmailLogEntity emailLogEntity = new EmailLogEntity();
				emailLogEntity.setEmailId(mailId);
				emailLogEntity.setLogMethod(logMethod);
				emailLogEntity.setReqLogSub(ReqSub);
				emailLogEntity.setReqLog(message);
				emailLogEntity.setResponseLog(emailResponse);
				emailLogRepository.save(emailLogEntity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Method to create smsLogMethod
	 * 
	 * @author Vennila
	 * @param SmsLogEntity
	 * @return
	 */

	public void storeSmsLog(String request, String smsResponse, String logMethod, long mobileNumber) {
		if (request == null || smsResponse == null || logMethod == null) {
			// Handle invalid input, such as throwing an IllegalArgumentException.
			throw new IllegalArgumentException("Request, smsResponse, or logMethod cannot be null.");
		}
		try {
			SmsLogEntity smsLogEntity = new SmsLogEntity();
			smsLogEntity.setMobileNo(mobileNumber);
			smsLogEntity.setLogMethod(logMethod);
			smsLogEntity.setRequestLog(request);
			smsLogEntity.setResponseLog(smsResponse);
			smsLogRepository.save(smsLogEntity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
