package in.codifi.api.restservice;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import in.codifi.api.request.model.VerifyUserRequest;

@RegisterRestClient(configKey = "config-keylock")
@RegisterClientHeaders
public interface IKeylockRestService {
	
	@Path("/user/verify")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponse(description = "")
	String verifyUserinKeycloak(@RequestBody VerifyUserRequest verifyUserRequest);

	@Path("user/create/guest")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponse(description = "")
	String createGuestUserinKeycloak(VerifyUserRequest verifyUserRequest);

	@Path("user/update/activeuser")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponse(description = "")
	String UpdateActiveUSer(VerifyUserRequest verifyUserRequest);

}
