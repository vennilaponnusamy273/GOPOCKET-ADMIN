package in.codifi.api.request.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyUserRequest {

	private String emailId;
	private String mobile;	
	private String password;
	
	private String pan;
	private String userId;	
	private String firstName;	
	private String lastName;
	private String ucc;
}
