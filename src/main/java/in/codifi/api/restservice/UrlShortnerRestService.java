package in.codifi.api.restservice;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.json.JSONObject;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.response.model.ResponseModel;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;


@ApplicationScoped
public class UrlShortnerRestService {
	private static final Logger logger = LogManager.getLogger(UrlShortnerRestService.class);
	@Inject
	CommonMethods commonMethods;
	@Inject
	ApplicationProperties properties;
	@Inject
	@RestClient
	IUrlShortnerRestService iUrlShortnerRestService;

	/**Not working**/
	public String shortenUrl(String longUrl) {
		String response = null;
		try {
			String shortUrl = iUrlShortnerRestService.getUrlShortner(properties.getIvrUrlShortnerToken(),
					URLEncoder.encode(longUrl, StandardCharsets.UTF_8));
			System.out.println("thw shortUrl new"+shortUrl);
			JSONObject responseJson = new JSONObject(shortUrl);
			shortUrl = responseJson.getJSONObject(EkycConstants.URL).getString(EkycConstants.SHORT_URL);
			System.out.println("thw shortUrl"+shortUrl);
			response= shortUrl;
		} catch (Exception e) {
			
			logger.error("An error occurred: " + e.getMessage());
			ResponseModel responseModel = new ResponseModel();
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
			response = responseModel.toString();
		}
		return response;
	}
	
	/**
	 * Method to shorten the url
	 * 
	 * @param longUrl
	 * @return
	 */
	public String generateShortLink(String longUrl) {
		HttpURLConnection conn = null;
		String shortUrl = "";
		try {
			String apiKey = properties.getIvrUrlShortnerToken();
			String apiUrl = String.format(properties.getBitlyBaseUrl(), apiKey,
					URLEncoder.encode(longUrl, StandardCharsets.UTF_8));
			URL url = new URL(apiUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(EkycConstants.HTTP_GET);
			conn.setRequestProperty(EkycConstants.IVR_ACCEPT, EkycConstants.CONSTANT_APPLICATION_JSON);
			if (conn.getResponseCode() != 200) {
				BufferedReader errorReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				String errorOutput;
				StringBuilder errorResponseBuilder = new StringBuilder();
				while ((errorOutput = errorReader.readLine()) != null) {
					errorResponseBuilder.append(errorOutput);
				}
				throw new RuntimeException(MessageConstants.FAILED_HTTP_CODE + conn.getResponseCode() + " : "
						+ errorResponseBuilder.toString());
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String output;
			StringBuilder responseBuilder = new StringBuilder();
			while ((output = in.readLine()) != null) {
				responseBuilder.append(output);
			}
			JSONObject responseJson = new JSONObject(responseBuilder.toString());
			JSONObject urlObj = responseJson.getJSONObject(EkycConstants.URL);
			shortUrl = urlObj.getString(EkycConstants.SHORT_URL);
		} catch (Exception e) {
			commonMethods.sendErrorMail(
					"An error occurred while processing your request, In generateShortLink for the Error: "
							+ e.getMessage(),
					"ERR-001");
			commonMethods.SaveLog(null, "IvrService", "generateShortLink", e.getMessage());
			logger.error("An error occurred: " + e.getMessage());
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return shortUrl;
	}
}
