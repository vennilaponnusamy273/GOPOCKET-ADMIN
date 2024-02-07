package in.codifi.api.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import in.codifi.api.request.model.VerifyUserRequest;
import in.codifi.api.restservice.KeylockRestService;
import in.codifi.api.service.spec.IkeylockService;
import in.codifi.api.utilities.CommonMethods;

@ApplicationScoped
public class keylockService implements IkeylockService{
	
	@Inject
	CommonMethods commonMethods;
	@Inject
	KeylockRestService keylockRestService;

	private static final Logger logger = LogManager.getLogger(keylockService.class);
	
	
	
	
	@Override
	public String  UpdateActiveUSer(VerifyUserRequest verifyUserRequest) {
		String Response=null;
		try {
		Response=keylockRestService.UpdateActiveUSer(verifyUserRequest);
		System.out.println("the response UpdateActiveUSer"+Response);	
	} catch (Exception e) {
		logger.error("An error occurred: " + e.getMessage());
		//commonMethods.SaveLog(EmailID, "keylockService", "verifyUserinKeycloak", e.getMessage());
		commonMethods
				.sendErrorMail("An error occurred while processing your request, In verifyUserinKeycloak for this Error :"
						+ e.getMessage(), "ERR-001");
	}
	return Response;
}

	
}
