package in.codifi.api.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.validation.constraints.NotNull;

import in.codifi.api.entity.ApiStatusEntity;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.entity.DocumentEntity;
import in.codifi.api.entity.EmailTemplateEntity;
import in.codifi.api.repository.ApiStatusRepository;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.repository.DocumentEntityRepository;
import in.codifi.api.repository.EmailTemplateRepository;
import in.codifi.api.repository.KraKeyValueRepository;
import in.codifi.api.request.model.ApiStatusModel;
import in.codifi.api.response.model.ResponseModel;
import in.codifi.api.service.spec.IApiStatusService;
import in.codifi.api.utilities.CommonMail;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;

@ApplicationScoped
public class ApiStatusService implements IApiStatusService {

	@Inject
	CommonMail commonMail;
	@Inject
	CommonMethods commonMethods;
	@Inject
	ApiStatusRepository apiStatusRepository;
	@Inject
	KraKeyValueRepository kraKeyValueRepository;
	@Inject
	EmailTemplateRepository emailTemplateRepository;
	@Inject
	ApplicationUserRepository applicationUserRepository;
	@Inject
	DocumentEntityRepository documentRepository;

	@Override
	public ResponseModel updateStatus(ApiStatusModel apiStatusModel) {
		ResponseModel response = new ResponseModel();

		try {
			int status = apiStatusModel.getStatus();
			String stage = apiStatusModel.getStage();

			if (status == 0 || status == 1) {
				boolean isPageDocument = stage.equals(EkycConstants.PAGE_DOCUMENT);
				ApiStatusEntity checkExit;

				if (isPageDocument) {
					checkExit = apiStatusRepository.findByApplicationIdAndStageAndDocType(
							apiStatusModel.getApplicationId(), stage, apiStatusModel.getDocType());
					if (checkExit != null) {
						DocumentEntity documentEntity = documentRepository.findByApplicationIdAndDocumentType(
								apiStatusModel.getApplicationId(), apiStatusModel.getDocType());
						if (documentEntity != null) {
							documentEntity.setIsRejected(status == 0 ? 1 : 0);
							documentEntity.setIsApproval(status);
							documentRepository.save(documentEntity);
						}
					}
				} else {
					checkExit = apiStatusRepository.findByApplicationIdAndStage(apiStatusModel.getApplicationId(),
							stage);
				}

				if (checkExit == null) {
					ApiStatusEntity apiStatusEntity = new ApiStatusEntity();
					apiStatusEntity.setApplicationId(apiStatusModel.getApplicationId());
					apiStatusEntity.setStage(stage);
					apiStatusEntity.setStatus(status);
					apiStatusEntity.setApprovedBy(apiStatusModel.getApprovedBy());
					apiStatusEntity.setReason(apiStatusModel.getReason());

					if (isPageDocument) {
						apiStatusEntity.setDocType(apiStatusModel.getDocType());
					}
					checkExit = apiStatusEntity;
				} else {
					checkExit.setReason(apiStatusModel.getReason());
					checkExit.setStatus(status);
					checkExit.setApprovedBy(apiStatusModel.getApprovedBy());
				}

				apiStatusRepository.save(checkExit);
				response.setMessage(EkycConstants.SUCCESS_MSG);
				response.setStat(EkycConstants.SUCCESS_STATUS);
				response.setResult(checkExit);
			} else {
				response = commonMethods.constructFailedMsg(MessageConstants.STATUSINVALID);
			}
		} catch (Exception e) {
			response = commonMethods.constructFailedMsg(e.getMessage());
		}

		return response;
	}

	@Override
	public ResponseModel getStatus(long applicationId) {
		ResponseModel response = new ResponseModel();
		try {
			List<ApiStatusEntity> checkExit = apiStatusRepository.findByApplicationId(applicationId);
			if (checkExit != null && !checkExit.isEmpty()) {
				response.setResult(checkExit);
				response.setMessage(EkycConstants.SUCCESS_MSG);
				response.setStat(EkycConstants.SUCCESS_STATUS);
			} else {
				response = commonMethods.constructFailedMsg(MessageConstants.STATUSNOTFOUND);
			}

		} catch (Exception e) {
			response = commonMethods.constructFailedMsg(e.getMessage());
		}
		return response;
	}

	@Override
	public ResponseModel sendMail(@NotNull long applicationId) {
		ResponseModel response = new ResponseModel();
		try {
			List<ApiStatusEntity> checkExit = apiStatusRepository.findByApplicationId(applicationId);
			Optional<ApplicationUserEntity> user = applicationUserRepository.findById(applicationId);

			if (user.isPresent()) {
				if (checkExit != null && !checkExit.isEmpty()) {
					StringBuilder stagesBuilder = new StringBuilder();
					StringBuilder reasonsBuilder = new StringBuilder();
					boolean sendMail = false;

					for (ApiStatusEntity mailSend : checkExit) {
						if (mailSend.getStatus() == 0) {
							String stage = mailSend.getStage();
							String stageFromKra = kraKeyValueRepository.getkeyValueForKra("11", "STAGE", stage);
							String reason = mailSend.getReason();
							stagesBuilder.append(stageFromKra).append(" AND ");
							reasonsBuilder.append(reason).append(" AND ");
							sendMail = true;
						}
					}

					if (sendMail) {
						String stages = stagesBuilder.toString().replaceAll(" AND $", "").trim();
						String reasons = reasonsBuilder.toString().replaceAll(" AND $", "").trim();
						sendMailOtp(stages, reasons, user.get().getEmailId(), user.get().getUserName());
						response.setMessage(EkycConstants.SUCCESS_MSG);
						response.setStat(EkycConstants.SUCCESS_STATUS);
						response.setResult("Message sent successfully");
					}

				} else {
					response = commonMethods.constructFailedMsg(MessageConstants.STATUSNOTFOUND);
				}
			} else {
				response = commonMethods.constructFailedMsg(MessageConstants.USER_ID_INVALID);
			}
		} catch (Exception e) {
			response = commonMethods.constructFailedMsg(e.getMessage());
		}

		return response;
	}

	/**
	 * Method to send mail
	 * 
	 * @param stages
	 * @param reasons
	 * @throws MessagingException
	 */
	public void sendMailOtp(String stages, String reasons, String emailId, String username) throws MessagingException {
		EmailTemplateEntity emailTempentity = emailTemplateRepository.findByKeyData("doc");
		try {
			List<String> toAdd = new ArrayList<>();
			toAdd.add(emailId);
			String body_Message = emailTempentity.getBody();
			String body = body_Message.replace("{stage}", stages).replace("{corrections}", reasons)
					.replace("{UserName}", username);
			String subject = emailTempentity.getSubject();
			List<String> bcc = Collections.singletonList("ekycsupport@gopocket.in");
			commonMail.sendMail(toAdd, subject, body,bcc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
