package in.codifi.api.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import in.codifi.api.controller.spec.backOfficeApiController;
import in.codifi.api.response.model.ResponseModel;
import in.codifi.api.service.spec.IbackOfficeApiService;

@Path("/BackOffice")
public class IbackOfficeApiController implements backOfficeApiController{
	@Inject
	IbackOfficeApiService ibackOfficeApiService;
	
	@Override
	public ResponseModel updateBackoffice(long applicationId) {
		ResponseModel responseModel=new ResponseModel();
	if(applicationId>0) {
		responseModel=ibackOfficeApiService.callBckOfficeAPI(applicationId);
	}
		return responseModel;
	}
	
	@Override
	public ResponseModel sendRiskDisClosure(long applicationId) {
		ResponseModel responseModel=new ResponseModel();
	if(applicationId>0) {
		responseModel=ibackOfficeApiService.sendRiskDoc(applicationId);
	}
		return responseModel;
	}

}
