package in.codifi.api.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.entity.EmailTemplateEntity;
import in.codifi.api.entity.ReferralEntity;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.repository.EmailTemplateRepository;
import in.codifi.api.repository.ReferralRepository;
import in.codifi.api.response.model.ResponseModel;
import in.codifi.api.restservice.SmsRestService;
import in.codifi.api.restservice.UrlShortnerRestService;
import in.codifi.api.service.spec.IReferralService;
import in.codifi.api.utilities.CommonMail;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;
import in.codifi.api.utilities.StringUtil;

@ApplicationScoped
public class ReferralService implements IReferralService {

	private static final Logger logger = LogManager.getLogger(ReferralService.class);

	@Inject
	CommonMethods commonMethods;
	@Inject
	CommonMail commonMail;
	@Inject
	ApplicationProperties props;
	@Inject
	ApplicationUserRepository applicationUserRepository;
	@Inject
	ReferralRepository notifyRepository;
	@Inject
	SmsRestService ismsRestService;
	@Inject
	EmailTemplateRepository emailTemplateRepository;
	@Inject
	UrlShortnerRestService restService;

	@Override
	public ResponseModel setReferral(ReferralEntity NotifyEntity) {
		ResponseModel response = new ResponseModel();
		try {
			ApplicationUserEntity applicationUserEntity = applicationUserRepository
					.findByMobileNo(NotifyEntity.getMobileNo());
			if (NotifyEntity.getId() == null || NotifyEntity.getId() <= 0) {
				if (applicationUserEntity == null) {
					ReferralEntity existingNotifyEntity = notifyRepository.findByMobileNo(NotifyEntity.getMobileNo());
					if (existingNotifyEntity == null) {
						if (NotifyEntity.getMobileNo() != null && NotifyEntity.getMobileNo() > 0) {
							NotifyEntity.setUrl(MessageConstants.EKYC_URL + NotifyEntity.getReferralBy());
							notifyRepository.save(NotifyEntity);
							response = sendSmsAndEmail(existingNotifyEntity);
							response.setResult(NotifyEntity);
						} else {
							response.setStat(EkycConstants.SUCCESS_STATUS);
							response.setMessage(EkycConstants.SUCCESS_MSG);
							response.setReason("Mobile number Is Empty");
						}
					} else {
						response.setStat(EkycConstants.SUCCESS_STATUS);
						response.setMessage(EkycConstants.SUCCESS_MSG);
						response.setReason("Already details Available in Referral Table");
					}
				} else {
					response.setStat(MessageConstants.SUCCESS_STATUS);
					response.setMessage(MessageConstants.SUCCESS_MSG);
					response.setReason("MobileNumber already available in Master table");
				}
			} else {
				ReferralEntity existingNotifyEntity = notifyRepository.findByMobileNo(NotifyEntity.getMobileNo());
				if (existingNotifyEntity != null) {
					existingNotifyEntity.setEmailId(NotifyEntity.getEmailId());
					existingNotifyEntity.setPanNumber((NotifyEntity.getPanNumber()));
					notifyRepository.save(existingNotifyEntity);
					response = sendSmsAndEmail(existingNotifyEntity);
					response.setResult(existingNotifyEntity);
				}

			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(null, "nodifyService", "modifyUser", e.getMessage());
			commonMethods.sendErrorMail(
					"An error occurred while processing your request. In EkycAdmin modifyUser for the Error: "
							+ e.getMessage(),
					"ERR-001");
			response = commonMethods.constructFailedMsg(e.getMessage());
		}
		return response;
	}

	public void sendMessagetoMobile(String Message, long mobileNumber) {
		try {
			String shortenUrl = restService.generateShortLink(Message);
			System.out.println("the shortenUrl" + shortenUrl);
			ismsRestService.sendSms(shortenUrl, mobileNumber);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to nodify user to Mail and Message
	 */
	@Override
	public ResponseModel notifyUser(@NotNull long id, @NotNull String referralId) {
		ResponseModel response = new ResponseModel();
		ReferralEntity existingNotifyEntity = notifyRepository.findByIdAndReferralBy(id, referralId);
		if (existingNotifyEntity != null) {
			response = sendSmsAndEmail(existingNotifyEntity);
		} else {
			response = commonMethods.constructFailedMsg(MessageConstants.INVLAID_PARAMETER);
		}
		return response;
	}

	/**
	 * get Referral Record by referral Id
	 */
	@Override
	public ResponseModel getRecordByUser(@NotNull String referralId) {
		ResponseModel response = new ResponseModel();
		List<ReferralEntity> ReferralEntities = notifyRepository.findByReferralBy(referralId);
		if (StringUtil.isListNotNullOrEmpty(ReferralEntities)) {
			response.setStat(EkycConstants.SUCCESS_STATUS);
			response.setMessage(EkycConstants.SUCCESS_MSG);
			response.setResult(ReferralEntities);
		} else {
			response = commonMethods.constructFailedMsg(MessageConstants.NO_RECORD_FOUND);
		}
		return response;
	}

	public ResponseModel sendSmsAndEmail(ReferralEntity NotifyEntity) {
		ResponseModel response = new ResponseModel();
		if (NotifyEntity != null) {
			sendMessagetoMobile(NotifyEntity.getUrl(), NotifyEntity.getMobileNo());
			if (StringUtil.isNotNullOrEmpty(NotifyEntity.getEmailId())) {
				EmailTemplateEntity emailTemplateEntity = emailTemplateRepository.findByKeyData("referral");
				if (emailTemplateEntity != null && emailTemplateEntity.getBody() != null
						&& emailTemplateEntity.getSubject() != null) {
					List<String> toAdd = new ArrayList<>();
					toAdd.add(NotifyEntity.getEmailId());
					String bodyMessage = emailTemplateEntity.getBody();
					String body = bodyMessage.replace("{Link}", NotifyEntity.getUrl());
					String subject = emailTemplateEntity.getSubject();
					List<String> bcc = Collections.singletonList("ekycsupport@gopocket.in");
					commonMail.sendMail(toAdd, subject, body,bcc);
				}
			}
			response.setStat(EkycConstants.SUCCESS_STATUS);
			response.setMessage(EkycConstants.SUCCESS_MSG);
			response.setReason("Notification Successfully Sent..!");
		}
		return response;
	}
}
