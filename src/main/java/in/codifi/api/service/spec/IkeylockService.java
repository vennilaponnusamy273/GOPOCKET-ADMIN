package in.codifi.api.service.spec;

import in.codifi.api.request.model.VerifyUserRequest;
import in.codifi.api.response.model.ResponseModel;

public interface IkeylockService {

	
	ResponseModel UpdateActiveUSer(VerifyUserRequest verifyUserRequest);
}
