package in.codifi.api.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "tbl_application_master")
public class ApplicationUserEntity extends CommonEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "user_name")
	private String userName;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "middle_name")
	private String middleName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "mobile_number")
	private Long mobileNo;

	@Column(name = "email_id")
	private String emailId;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(name = "email_otp")
	private int emailOtp;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(name = "sms_otp")
	private int smsOtp;

	@Column(name = "email_verified")
	private int emailVerified;

	@Column(name = "sms_verified")
	private int smsVerified;

	@Column(name = "pan_number")
	private String panNumber;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(name = "pwd")
	private String password;

	@Column(name = "dob")
	private String dob;

	@Column(name = "stage")
	private String stage;

	@Column(name = "status")
	private String status;

	@Column(name = "esign_completed")
	private int esignCompleted = 0;

	@Column(name = "pdf_generated")
	private Integer pdfGenerated = 0;

	@Column(name = "pan_confirm")
	private int panConfirm;

	@Column(name = "pan_status_code")
	private String panStatusCode;

	@Column(name = "nominee_opted_out")
	private int nomineeOptedOut = 0;

	@Column(name = "esiged_name")
	private String esigedName;

	@Column(name = "kra_response_date")
	private String kraResponseDate;

	@Column(name = "ucc_code_prefix")
	private String uccCodePrefix;

	@Column(name = "ucc_code_suffix")
	private String uccCodeSuffix;
	
	@Column(name = "aadharpan_link")
	private String aadharPanLink;

	@Transient
	private String gender;

	@Transient
	private String authToken;
}