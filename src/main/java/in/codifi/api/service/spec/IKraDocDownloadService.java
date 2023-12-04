package in.codifi.api.service.spec;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;

public interface IKraDocDownloadService {

	/**
	 * Method to download uploaded file
	 * 
	 * @param applicationId and DocType
	 * @param type
	 * @return
	 */
	Response downloadkraFile(@NotNull long applicationId);

}
