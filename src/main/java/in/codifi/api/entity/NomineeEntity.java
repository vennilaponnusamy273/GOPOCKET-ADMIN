package in.codifi.api.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

@Entity(name = "tbl_nominee_details")
@Getter
@Setter
public class NomineeEntity extends CommonEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "application_id", nullable = false)
	private Long applicationId;

	@Column(name = "nominee_id")
	private String nomineeId;

	@Column(name = "first_name")
	private String firstname;

	@Column(name = "last_name")
	private String lastname;

	@Column(name = "relationship")
	private String relationship;

	@Column(name = "dob")
	private String dateOfbirth;

	@Column(name = "typeOfProof",nullable = false)
	private String typeOfProof;
	
	@Column(name = "proofId")
	private String proofId;

	@Column(name = "mobile_number", length = 10)
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

	@Column(name = "allocation")
	private int allocation;

	@Transient
	private GuardianEntity guardianEntity;

	@Transient
	private int nomOneAllocation;

	@Transient
	private int nomTwoAllocation;

	@Transient
	private int nomThreeAllocation;

}
