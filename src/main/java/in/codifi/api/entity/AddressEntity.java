package in.codifi.api.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "tbl_address_details")
public class AddressEntity extends CommonEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "address_confirm")
	private int addressConfirm;

	@Column(name = "is_digi")
	private int isdigi;

	@Column(name = "access_token")
	private String accessToken;

	@Column(name = "application_id")
	private Long applicationId;

	@Column(name = "flatno")
	private String flatNo;

	@Column(name = "digi_cur_address")
	private String digiCurAddress;

	@Column(name = "digi_cur_locality")
	private String digiCurLocality;

	@Column(name = "digi_cur_district")
	private String digiCurDistrict;

	@Column(name = "digi_cur_state")
	private String digiCurState;

	@Column(name = "digi_cur_country")
	private String digiCurCountry;

	@Column(name = "digi_cur_pincode")
	private String digiCurPincode;

	@Column(name = "digi_per_address")
	private String digiPerAddress;

	@Column(name = "digi_per_locality")
	private String digiPerLocality;

	@Column(name = "digi_per_district")
	private String digiPerDistrict;

	@Column(name = "digi_per_state")
	private String digiPerState;

	@Column(name = "digi_per_country")
	private String digiPerCountry;

	@Column(name = "digi_dob")
	private String digidob;
	
	@Column(name = "digi_name")
	private String diginame;
	
	@Column(name = "digi_gender")
	private String digigender;
	
	@Column(name = "digi_per_pincode")
	private String digiPerPincode;


	@Column(name = "aatharNo")
	private String aadharNo;
	
	@Column(name = "is_kra")
	private int isKra;

	@Column(name = "kra_address_1")
	private String kraAddress1;

	@Column(name = "kra_address_2")
	private String kraAddress2;

	@Column(name = "kra_address_3")
	private String kraAddress3;

	@Column(name = "kra_pin")
	private int kraPin;

	@Column(name = "kra_city")
	private String kraCity;

	@Column(name = "kra_state")
	private String kraState;

	@Column(name = "kra_country")
	private String kraCountry;

	@Column(name = "kra_per_address_1")
	private String kraPerAddress1;

	@Column(name = "kra_per_address_2")
	private String kraPerAddress2;

	@Column(name = "kra_per_address_3")
	private String kraPerAddress3;

	@Column(name = "kra_per_pin")
	private int kraPerPin;

	@Column(name = "kra_per_city")
	private String kraPerCity;

	@Column(name = "kra_per_state")
	private String kraPerState;

	@Column(name = "kra_per_country")
	private String kraPerCountry;
	
	
	@Column(name = "kra_address_proof")
	private String kraaddressproof;
	
	@Column(name = "kra_proof_IdNumber")
	private String kraproofIdNumber;


}
