package in.codifi.api.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity(name = "tbl_guardian_details")
@Getter
@Setter
public class GuardianEntity extends CommonEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "application_id")
	private Long applicationId;

	@Column(name = "nominee_id")
	private Long nomineeId;

	@Column(name = "first_name")
	private String firstname;

	@Column(name = "last_name")
	private String lastname;

	@Column(name = "relationship")
	private String relationship;

	@Column(name = "dob")
	private String dateOfbirth;

	@Column(name = "typeOfProof")
	private String typeOfProof;
	
	@Column(name = "proofId")
	private String proofId;

	@Column(name = "mobile_number")
	private long mobilenumber;

	@Column(name = "email_address")
	private String emailaddress;

	@Column(name = "address1")
	private String address1;

	@Column(name = "address2")
	private String address2;

	@Column(name = "pincode")
	private Long pincode;

	@Column(name = "state")
	private String state;

	@Column(name = "attachementUrl")
	private String attachementUrl;

}
