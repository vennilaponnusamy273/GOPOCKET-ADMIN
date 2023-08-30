package in.codifi.api.restservice;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
@RegisterRestClient(configKey = "config-bitlybase")
@RegisterClientHeaders
public interface IUrlShortnerRestService {
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponse(description = "")
	String getUrlShortner(@QueryParam("key") String apiKey, @QueryParam("short") String apiUrl) throws Exception;

}
