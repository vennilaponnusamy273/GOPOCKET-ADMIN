package in.codifi.api.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import in.codifi.api.controller.spec.IAdminController;
import in.codifi.api.response.model.ResponseModel;
import in.codifi.api.restservice.SmsRestService;
import in.codifi.api.service.spec.IAdminService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.MessageConstants;
import in.codifi.api.utilities.StringUtil;
import io.smallrye.common.constraint.NotNull;

@Path("/admin")
public class AdminController implements IAdminController {

	@Inject
	IAdminService adminService;
	@Inject
	CommonMethods commonMethods;
	@Inject
	SmsRestService restService;
	@Inject
	IbackOfficeApiController ibackOfficeApiController;
	/**
	 * Method to send mail on Esign Users
	 */
	@Override
	public ResponseModel sendRejectionMail(@NotNull long applicationId, boolean confirmMail) {
		ResponseModel responseModel = null;
		if (applicationId > 0) {
			responseModel = adminService.sendRejectionMail(applicationId, confirmMail);
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.INVLAID_PARAMETER);
		}
		return responseModel;
	}

	/**
	 * Method to initiaze push to back office
	 */
	@Override
	public ResponseModel pushBO(long applicationId) {
		ResponseModel responseModel = null;
		if (applicationId > 0) {
			responseModel = ibackOfficeApiController.updateBackoffice(applicationId);
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
		}
		return responseModel;
	}

	/**
	 * method to get ifsc
	 * 
	 * @author SOWMIYA
	 * 
	 * @param bankEntity
	 * @return
	 */
	public ResponseModel getIfsc(String ifscCode) {
		ResponseModel responseModel = new ResponseModel();
		if (StringUtil.isNotNullOrEmpty(ifscCode)) {
			responseModel = adminService.getIfsc(ifscCode);
		} else {
			responseModel = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
		}
		return responseModel;
	}

	@Override
	public ResponseModel test() {
		restService.sendSms("aabc", 8526707787l);
		return null;
	}

}
