package in.codifi.api.controller.spec;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import in.codifi.api.response.model.ResponseModel;
import io.smallrye.common.constraint.NotNull;

public interface backOfficeApiController {

	@Path("/backoffice")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to get bank Details")
	ResponseModel updateBackoffice(@NotNull @QueryParam("applicationId") long applicationId);
	
	@Path("/sendRiskDisClosure")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to get bank Details")
	ResponseModel sendRiskDisClosure(@NotNull @QueryParam("applicationId") long applicationId);
}
