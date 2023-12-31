package in.codifi.api.service;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.validation.constraints.NotNull;

import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.entity.CheckApiEntity;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.repository.CheckApiRepository;
import in.codifi.api.request.model.BankAddressModel;
import in.codifi.api.response.model.ResponseModel;
import in.codifi.api.service.spec.IAdminService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;

@ApplicationScoped
public class AdminService implements IAdminService {

	@Inject
	ApplicationUserRepository applicationUserRepository;
	@Inject
	CheckApiRepository apiRepository;
	@Inject
	CommonMethods commonMethods;

	@Override
	public ResponseModel sendRejectionMail(@NotNull long applicationId, boolean confirmMail) {
		ResponseModel responseModel = new ResponseModel();
		Optional<ApplicationUserEntity> userEntity = applicationUserRepository.findById(applicationId);
		if (userEntity != null) {
			CheckApiEntity apiEntity = apiRepository.findByapplicationId(applicationId);
			if (confirmMail) {
				// confirmMail is true (1)
				if (apiEntity == null) {
					apiEntity = new CheckApiEntity();
					apiEntity.setRejectionMail(1);
					apiEntity.setApplicationId(applicationId);
				} else {
					int rejectionMailcount = apiEntity.getRejectionMail();
					apiEntity.setRejectionMail(rejectionMailcount + 1);
				}
				apiRepository.save(apiEntity);
				try {
					commonMethods.sendRejectionMail(userEntity.get().getUserName(), userEntity.get().getEmailId(),applicationId);
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				responseModel.setMessage(EkycConstants.SUCCESS_MSG);
				responseModel.setStat(EkycConstants.SUCCESS_STATUS);
				responseModel.setReason("Mail sent successfully");
			} else {
				// confirmMail is false (0)
				responseModel.setMessage(EkycConstants.SUCCESS_MSG);
				responseModel.setStat(EkycConstants.SUCCESS_STATUS);
				responseModel.setReason("Mail sending confirmation value is false");
			}
		}
		return responseModel;
	}


	/**
	 * Method to initiaze push to back office
	 */
	@Override
	public ResponseModel pushBO(@NotNull long applicationId) {
		ResponseModel responseModel = new ResponseModel();
		CheckApiEntity apiEntity = apiRepository.findByapplicationId(applicationId);
		if (apiEntity == null) {
			apiEntity = new CheckApiEntity();
			apiEntity.setStartStatus(1l);
			apiEntity.setApplicationId(applicationId);
			// TODO push to back office
		} else {
			long count = apiEntity.getStartStatus();
			apiEntity.setStartStatus(count + 1);
		}
		apiRepository.save(apiEntity);
		responseModel = new ResponseModel();
		responseModel.setMessage(EkycConstants.SUCCESS_MSG);
		responseModel.setStat(EkycConstants.SUCCESS_STATUS);
		responseModel.setReason("Back Office push started successfully");
		return responseModel;
	}

	/**
	 * method to get ifsc
	 * 
	 * @author SOWMIYA
	 * 
	 */
	@Override
	public ResponseModel getIfsc(@NotNull String ifscCode) {
		ResponseModel responseModel = new ResponseModel();
		try {
			BankAddressModel model = commonMethods.findBankAddressByIfsc(ifscCode);
			if (model != null) {
				responseModel.setMessage(EkycConstants.SUCCESS_MSG);
				responseModel.setStat(EkycConstants.SUCCESS_STATUS);
				responseModel.setResult(model);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.IFSC_INVALID);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseModel;
	}
}
