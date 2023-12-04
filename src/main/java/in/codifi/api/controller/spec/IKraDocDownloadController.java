package in.codifi.api.controller.spec;

import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

public interface IKraDocDownloadController {

	/**
	 * Method to download kra pdf file
	 * 
	 * @param applicationId and docType
	 * @param type
	 * @return
	 */
	@Path("/getKraFile")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to download kra document ")
	public Response downloadKraFile(@NotNull @QueryParam("applicationId") long applicationId);
}
