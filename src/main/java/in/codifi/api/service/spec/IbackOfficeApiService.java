package in.codifi.api.service.spec;

import in.codifi.api.response.model.ResponseModel;

public interface IbackOfficeApiService {

	ResponseModel callBckOfficeAPI(long applicationId);

	ResponseModel sendRiskDoc(long applicationId);

}
