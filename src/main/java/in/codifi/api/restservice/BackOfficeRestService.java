package in.codifi.api.restservice;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.utilities.CommonMethods;


@ApplicationScoped
public class BackOfficeRestService {

	@Inject
	@RestClient
	IBackOfficeRestService iBackOfficeRestService;
	@Inject
	ApplicationProperties props;
	@Inject
	CommonMethods commonMethods;

	public String getStateCodeListonBO(String state) {
        String stateCode = null;

        try {
            String requestBody = "{\r\n\r\n    \"key\": \"" + props.getBackofficeKey() + "\",\r\n\r\n    \"cSearch\": \"" + state + "\"\r\n\r\n}";
            
            // Assuming iBackOfficeRestService.getStatecode returns javax.ws.rs.core.Response
            Response response = iBackOfficeRestService.getStatecode(requestBody);

            // Ensure the response has a successful status code (200)
            if (response.getStatus() == 200) {
                // Extract the entity (response body) from the Response
                String responseBody = response.readEntity(String.class);
                System.out.println("the responseBody"+responseBody);
                // Parse the JSON response
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(responseBody);

                if (rootNode.isArray() && rootNode.size() > 0) {
                    JsonNode firstNode = rootNode.get(0);
                    stateCode = firstNode.path("GSTSTATECD").asText();
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); 
        }
        return stateCode;
	}

	public String getKRAStateCityList(String stateorcountry) {
	    String kraStateCity = null;

	    try {
	        String requestBody = "{\r\n\r\n    \"key\": \"" + props.getBackofficeKey() + "\",\r\n\r\n    \"cSearch\": \"" + stateorcountry + "\"\r\n\r\n}";

	        // Assuming iBackOfficeRestService.getKRAStateCityList returns javax.ws.rs.core.Response
	        Response response = iBackOfficeRestService.getKRAStateCityList(requestBody);

	        // Ensure the response has a successful status code (200)
	        if (response.getStatus() == 200) {
	            // Extract the entity (response body) from the Response
	            String responseBody = response.readEntity(String.class);
	            System.out.println("the responseBody"+responseBody);

	            // Parse the JSON response
	            ObjectMapper objectMapper = new ObjectMapper();
	            JsonNode rootNode = objectMapper.readTree(responseBody);

	            if (rootNode.isArray() && rootNode.size() > 0) {
	                JsonNode firstNode = rootNode.get(0);
	                kraStateCity = firstNode.path("KraStateCity").asText();
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    return kraStateCity;
	}


}
