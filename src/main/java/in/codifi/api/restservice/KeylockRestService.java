package in.codifi.api.restservice;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import in.codifi.api.request.model.VerifyUserRequest;



@ApplicationScoped
public class KeylockRestService {

	@Inject
	@RestClient
	IKeylockRestService IKeylockRestService;
	
	/**
	 * Method to verifyUserinKeycloak
	 * 
	 * @param model
	 * @return
	 */
	public String verifyUserinKeycloak(VerifyUserRequest verifyUserRequest)  {
		String apiModel = null;
		try {
			apiModel = IKeylockRestService.verifyUserinKeycloak(verifyUserRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return apiModel;
	}

	public String createGuestUserinKeycloak(VerifyUserRequest verifyUserRequest) {
		String apiModel = null;
		try {
			apiModel = IKeylockRestService.createGuestUserinKeycloak(verifyUserRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return apiModel;
	}

	public String UpdateActiveUSer(VerifyUserRequest verifyUserRequest) {
		String apiModel = null;
		try {
			apiModel = IKeylockRestService.UpdateActiveUSer(verifyUserRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return apiModel;
	}

}
