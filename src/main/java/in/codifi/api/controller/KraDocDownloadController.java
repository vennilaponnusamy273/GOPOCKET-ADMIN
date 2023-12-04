package in.codifi.api.controller;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import in.codifi.api.controller.spec.IKraDocDownloadController;
import in.codifi.api.service.spec.IKraDocDownloadService;
import in.codifi.api.utilities.MessageConstants;

@Path("/KraDocDownload")
public class KraDocDownloadController implements IKraDocDownloadController {
	
	@Inject
	IKraDocDownloadService kradocDownloadService;

	/**
	 * Method to download uploaded documents
	 */
	
	@Override
	public Response downloadKraFile(@NotNull long applicationId) {
		if (applicationId > 0 ) {
			return kradocDownloadService.downloadkraFile(applicationId);
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(MessageConstants.PARAMETER_NULL)
					.build();
		}
	}
}
