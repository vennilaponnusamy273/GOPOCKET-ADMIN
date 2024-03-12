package in.codifi.api.service;
import java.io.IOException;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.controller.spec.IAdminController;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.entity.BackOfficeApiEntity;
import in.codifi.api.helper.backOfficeHelper;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.repository.BackOfficeApiRepository;
import in.codifi.api.request.model.VerifyUserRequest;
import in.codifi.api.response.model.ResponseModel;
import in.codifi.api.service.spec.IbackOfficeApiService;
import in.codifi.api.utilities.CommonMail;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.MessageConstants;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@ApplicationScoped
public class backOfficeApiService implements IbackOfficeApiService {

	private static final Logger logger = LogManager.getLogger(backOfficeApiService.class);
	
	@Inject
	CommonMethods commonMethods;

	@Inject
	backOfficeHelper ibackOfficeHelper;
	
	@Inject
	IAdminController iAdminController;

	@Inject
	ApplicationProperties props;
	
	@Inject
	BackOfficeApiRepository backOfficeApiRepository;
	
	@Inject
	ApplicationUserRepository applicationUserRepository;
	
	@Inject 
	CommonMail commonMail;
	@Inject
	keylockService KeylockService;
	
	@Override
	public ResponseModel callBckOfficeAPI(long applicationId) {
	    ResponseModel responseModel = new ResponseModel();

	    try {
	        String apiUrl = props.getBackofficeApi();
	        OkHttpClient client = new OkHttpClient();
	        MediaType mediaType = MediaType.parse("application/json");
	        String jsonContent = ibackOfficeHelper.generateJsonContenet(applicationId);
	        System.out.println("the jsonContent" + jsonContent);
	        logger.debug("JSON Content: {}", jsonContent);

	        RequestBody body = RequestBody.create(mediaType, jsonContent);
	        CommonMethods.trustedManagement();
	        Request request = new Request.Builder()
	                .url(apiUrl)
	                .method("POST", body) // Use the appropriate method (POST or GET)
	                .header("Content-Type", "application/json")
	                .build();

	        try (Response response = client.newCall(request).execute()) {
	            if (response.isSuccessful()) {
	                String responseBody = response.body().string();
	                BackOfficeApiEntity backOfficeApiEntity = backOfficeApiRepository.findByapplicationId(applicationId);
	                if (backOfficeApiEntity == null) {
	                    backOfficeApiEntity = new BackOfficeApiEntity();
	                    backOfficeApiEntity.setApplicationId(applicationId);
	                }
	                backOfficeApiEntity.setJsonData(jsonContent);
	                backOfficeApiEntity.setReq(request.toString());
	                backOfficeApiEntity.setRes(responseBody);
	                backOfficeApiRepository.save(backOfficeApiEntity);
	                sendRiskDoc(applicationId);
	                // Use Jackson ObjectMapper to parse the JSON
	                ObjectMapper objectMapper = new ObjectMapper();
	                JsonNode jsonNode = objectMapper.readTree(responseBody);

	                // Extract the value of the "message" field
	                String message = jsonNode.get("message").asText();
	               // String message = "Record imported Successfully in SharePro";
	                if (message != null && message.contains("Record imported Successfully in SharePro")) {
	                    Optional<ApplicationUserEntity> userEntity = applicationUserRepository.findById(applicationId);
	                    if (userEntity.isPresent()&&userEntity.get().getUserName()!=null) {
	                        VerifyUserRequest verifyUserRequest = new VerifyUserRequest();
	                        verifyUserRequest.setPan(userEntity.get().getPanNumber());
	                        verifyUserRequest.setUserId(userEntity.get().getMobileNo().toString());
	                        verifyUserRequest.setFirstName(userEntity.get().getFirstName()!=null?userEntity.get().getFirstName():userEntity.get().getUserName());
	                        verifyUserRequest.setLastName(userEntity.get().getLastName());
	                        verifyUserRequest.setUcc(userEntity.get().getUccCodePrefix() + userEntity.get().getUccCodeSuffix());
	                        String keylockResponse=KeylockService.UpdateActiveUSer(verifyUserRequest);
	                        System.out.println("the keylockResponse"+keylockResponse);
	                        // Parse the JSON response
	                        JSONObject jsonResponse = new JSONObject(keylockResponse);

	                        // Extract values from the JSON object
	                        String keyclockstatus = jsonResponse.optString("status");
	                        String keyclockmessage = jsonResponse.optString("message");
	                        BackOfficeApiEntity backOfficeApiEntitykeylock = backOfficeApiRepository.findByapplicationId(applicationId);
	                        if(backOfficeApiEntitykeylock!=null) {
	                        	backOfficeApiEntitykeylock.setKeylockMessage(keyclockmessage);
	                        	backOfficeApiEntitykeylock.setKeylockStatus(keyclockstatus);
	                        	backOfficeApiEntitykeylock.setKeylockResponse(keylockResponse.toString());
	                        	backOfficeApiRepository.save(backOfficeApiEntitykeylock);
	                        }
	                    } else {
	                        System.out.println("User not found for applicationId: " + applicationId);
	                    }
	                }

	                logger.debug("Response Body: {}", responseBody);
	                responseModel.setResult(responseBody);
	            } else {
	                logger.error("API request failed with status code: {}", response.code());
	                logger.error("API URL: {}", apiUrl);
	                logger.error("Request Headers: {}", request.headers());
	                logger.error("Request Body: {}", jsonContent);
	                responseModel.setResult("API request failed with status code: " + response.code());
	            }} catch (IOException e) {
	            logger.error("An error occurred while making the API request.", e);
	            responseModel.setResult("An error occurred while making the API request: " + e.getMessage());
	        }
	    } catch (Exception e) {
	        logger.error("An error occurred.", e);
	        responseModel.setResult("An error occurred: " + e.getMessage());
	    }

	    return responseModel;
	}
	
	public ResponseModel sendRiskDoc(long applicationId) {
		ResponseModel response = new ResponseModel();
		try {
			Optional<ApplicationUserEntity> isUserPresent = applicationUserRepository.findById(applicationId);
			if (isUserPresent.isPresent()) {
				commonMail.sendRiskDocMail(isUserPresent.get().getEmailId(),isUserPresent.get().getUserName());
				response.setResult("Document send successfully");
			}else {
				response = commonMethods.constructFailedMsg(MessageConstants.WRONG_USER_ID);
			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(null, "AdminService", "sendRiskDoc", e.getMessage());
			commonMethods.sendErrorMail(
					"An error occurred while processing your request, In sendRiskDoc for the Error: " + e.getMessage(),
					"ERR-001");
			response = commonMethods.constructFailedMsg(e.getMessage());
		}
		return response;
	}

};