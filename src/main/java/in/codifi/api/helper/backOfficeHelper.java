package in.codifi.api.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.json.JSONException;

import com.google.gson.JsonObject;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.AddressEntity;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.entity.BankEntity;
import in.codifi.api.entity.GuardianEntity;
import in.codifi.api.entity.IvrEntity;
import in.codifi.api.entity.KraKeyValueEntity;
import in.codifi.api.entity.NomineeEntity;
import in.codifi.api.entity.PaymentEntity;
import in.codifi.api.entity.ProfileEntity;
import in.codifi.api.entity.ReferralEntity;
import in.codifi.api.entity.ResponseCkyc;
import in.codifi.api.entity.SegmentEntity;
import in.codifi.api.repository.AddressRepository;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.repository.BankRepository;
import in.codifi.api.repository.CkycResponseRepos;
import in.codifi.api.repository.GuardianRepository;
import in.codifi.api.repository.IvrRepository;
import in.codifi.api.repository.KraKeyValueRepository;
import in.codifi.api.repository.NomineeRepository;
import in.codifi.api.repository.PaymentRepository;
import in.codifi.api.repository.ProfileRepository;
import in.codifi.api.repository.ReferralRepository;
import in.codifi.api.repository.SegmentRepository;
import in.codifi.api.restservice.BackOfficeRestService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.StringUtil;

@ApplicationScoped
public class backOfficeHelper {
	
	@Inject
	ApplicationUserRepository applicationUserRepository;
	@Inject
	ProfileRepository profileRepository;
	@Inject
	BankRepository bankRepository;
	@Inject
	CommonMethods commonMethods;
	@Inject
	AddressRepository addressRepository;
	@Inject
	CkycResponseRepos ckycResponseRepos;
	@Inject
	IvrRepository ivrRepository;
	@Inject
	PaymentRepository paymentRepository;
	@Inject
	KraKeyValueRepository kraKeyValueRepository;
	@Inject
	ReferralRepository referralRepository;
	@Inject
	SegmentRepository segmentRepository;
	@Inject
	ApplicationProperties props;
	@Inject
	NomineeRepository nomineeRepository;
	@Inject
	GuardianRepository guardianRepository;
	@Inject
	BackOfficeRestService backOfficeRestService;
	
	public  String generateJsonContenet(long  applicationId ) throws ParseException, JSONException {
		 String jsonData = "";
		   Optional<ApplicationUserEntity> userEntity = applicationUserRepository.findById(applicationId);
	        ProfileEntity getProfile = profileRepository.findByapplicationId(applicationId);
	        BankEntity getBankDetails = bankRepository.findByapplicationId(applicationId);
	        AddressEntity getAddress = addressRepository.findByapplicationId(applicationId);
	        ResponseCkyc responseCkyc=ckycResponseRepos.findByApplicationId(applicationId);
	        IvrEntity ivr=ivrRepository.findByApplicationId(applicationId);
	        PaymentEntity paymentEntity=paymentRepository.findByApplicationId(applicationId);
	     // Get the current date
	        LocalDate currentDate = LocalDate.now();

	        // Create a DateTimeFormatter object with the desired format
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	        // Format the current date using the DateTimeFormatter
	        String formattedDate = currentDate.format(formatter);
	        if(userEntity!=null||getProfile!=null&&getBankDetails!=null||getAddress!=null&&responseCkyc!=null&&ivr!=null&&paymentEntity!=null) {
	        	JsonObject jsonObject = new JsonObject();
	            jsonObject.addProperty("key",props.getBackofficeKey());
	            jsonObject.addProperty("cUcc", userEntity.get().getUccCodePrefix()+userEntity.get().getUccCodeSuffix());
	            jsonObject.addProperty("cClientName", userEntity.get().getUserName());
	            jsonObject.addProperty("cFatherNm",getProfile.getFatherName());
	            jsonObject.addProperty("cMotherNm", getProfile.getMotherName());
	            String dobString = userEntity.get().getDob();
	            System.out.println("Original DOB: " + dobString);
	            // Define the date format of the input string
	            SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
	            Date dobDate = inputFormat.parse(dobString);
	            // Define the desired date format
	            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
	            String formattedDob = outputFormat.format(dobDate);

	            System.out.println("Formatted DOB: " + formattedDob);
	            // Format the Date to the desired output format;
	            jsonObject.addProperty("dBirth_Date",formattedDob);
	            String Gender=getProfile.getGender();
	            if(Gender!=null) {
	            	if (Gender.equalsIgnoreCase("Male")) {
	            		Gender="M";
	            	}else if(Gender.equalsIgnoreCase("Female")) {
	            		Gender="F";
	            	}else {
	            		Gender="N";
	            	}
	            }
	            jsonObject.addProperty("cGender", Gender);
	            String marriedStatus=getProfile.getMaritalStatus();
	            String cMaritalStatus=null;
	            if(marriedStatus!=null) {
	            	if(marriedStatus.equalsIgnoreCase("Single")){
	            		cMaritalStatus="02";
	            	}else if (marriedStatus.equalsIgnoreCase("Married")){
	            		cMaritalStatus="01";
	            	}else {
	            		cMaritalStatus="03";
	            	}
	            }
	            jsonObject.addProperty("cMaritalStatus", cMaritalStatus);
	            jsonObject.addProperty("dMarriageAniv", "");
	            jsonObject.addProperty("cNationality", "01");
	            jsonObject.addProperty("cResIndStatus","Resident Individual");
	            
	            String occupation = "";
	            String Kraoccupation = "";
				if (StringUtil.isEqual(getProfile.getOccupation(), "Private Sector")) {
					occupation = "Employed";
					Kraoccupation="01";
				} else if (StringUtil.isEqual(getProfile.getOccupation(), "Public Sector")) {
					occupation = "Employed";	
					Kraoccupation="02";
				} else if (StringUtil.isEqual(getProfile.getOccupation(), "Business")) {
					occupation = "Business";
					Kraoccupation="03";
				} else if (StringUtil.isEqual(getProfile.getOccupation(), "Professional")) {
					occupation = "Professional";
					Kraoccupation="04";
				} else if (StringUtil.isEqual(getProfile.getOccupation(), "House Wife")) {
					occupation = "House Wife";
					Kraoccupation="07";
				} else if (StringUtil.isEqual(getProfile.getOccupation(), "Student")) {
					occupation = "Student";
					Kraoccupation="08";
				} else if (StringUtil.isEqual(getProfile.getOccupation(), "Others")) {
					occupation = "Others";
					Kraoccupation="99";
				}	   
				
				  String income  = "";
				  String kraincome  = "";
				  if (StringUtil.isEqual(getProfile.getAnnualIncome(), "0-1 lakh")) {
					  kraincome = "01";
					  income = "Below Rs. 100000";
					} else if (StringUtil.isEqual(getProfile.getAnnualIncome(), "1-5 lakhs")) {
						kraincome = "02";
						income = "Rs. 100000 - 500000";				
					} else if (StringUtil.isEqual(getProfile.getAnnualIncome(), "5-10 lakhs")) {
						kraincome = "03";
						income = "Rs. 500000 - 1000000";
					} else if (StringUtil.isEqual(getProfile.getAnnualIncome(), "10-20 lakhs")) {
						kraincome = "04";
						income = "Rs. 1000000 - 2500000";
					} else if (StringUtil.isEqual(getProfile.getAnnualIncome(), "More than 20 lakhs")) {
						kraincome = "06";
						income = "above Rs. 2500000";
					}
	            jsonObject.addProperty("cOccupation",occupation);
	            jsonObject.addProperty("cAnnualIncome ",income);
	            jsonObject.addProperty("ClRiskProfile", "");
	            jsonObject.addProperty("cBranchID", "HO");
	            jsonObject.addProperty("cPan_No", userEntity.get().getPanNumber());
	            jsonObject.addProperty("cPassport", "");
	            jsonObject.addProperty("dPPDate", "");
	            jsonObject.addProperty("dPPExpDate", "");
	            jsonObject.addProperty("cVoterId", "");
	            jsonObject.addProperty("cRationId", "");
	            jsonObject.addProperty("cDriveLicense", "");
	            
	            
	            
	            jsonObject.addProperty("dDLDate", "");
	            jsonObject.addProperty("dDLExpDate ", "");
	            jsonObject.addProperty("cAadhaar", "");
	            jsonObject.addProperty("cPhotoIdPrf", "PAN Card");
	            String kraAddressProof = null; // Initialize the variable to null
	            kraAddressProof = getAddress.getKraaddressproof(); // Get the value from getAddress.getKraaddressproof()
	            // Check if kraAddressProof is equal to "AADHAAR" (ignoring case)
	            if(kraAddressProof!=null) {
	            	//System.out.println("THE RUNNNNNNNN");
	            if (kraAddressProof.equalsIgnoreCase("AADHAAR")) {
	                kraAddressProof = "ADHAAR"; // Set kraAddressProof to "ADHAAR"
	            } else {
	                kraAddressProof = getAddress.getKraaddressproof(); // Assign the original value back
	            }}

	            // Add a property "cAddPrf" to jsonObject with kraAddressProof as the value (or "ADHAAR" if it's null)
	            jsonObject.addProperty("cAddPrf", "ADHAAR");
	            StringBuilder addressBuilder = new StringBuilder();
	            if (getAddress != null) {
					   /** if (getAddress.getFlatNo() != null) {
					        addressBuilder.append(getAddress.getFlatNo());
					    }

					    if (getAddress.getStreet() != null) {
					        if (addressBuilder.length() > 0) {
					            addressBuilder.append(" ");
					        }
					        addressBuilder.append(getAddress.getStreet());
					    }
					    if (getAddress.getLandmark() != null) {
					        if (addressBuilder.length() > 0) {
					            addressBuilder.append(" ");
					        }
					        addressBuilder.append(getAddress.getLandmark());
					    }**/
					    if (getAddress.getDigiPerAddress() != null) {
					        if (addressBuilder.length() > 0) {
					            addressBuilder.append(" ");
					        }
					        addressBuilder.append(getAddress.getDigiPerAddress());
					    }}
					    String fullAddress = addressBuilder.toString();
					    String PerAddress1 = "";
					    String PerAddress2 = "";
					    String PerAddress3 = "";

					    if (fullAddress.length() <= 50) {
					        PerAddress1 = fullAddress;
					    } else if (fullAddress.length() <= 100) {
					        PerAddress1 = fullAddress.substring(0, 50);
					        PerAddress2 = fullAddress.substring(50);
					    } else {
					        PerAddress1 = fullAddress.substring(0, 50);
					        PerAddress2 = fullAddress.substring(50, 100);
					        PerAddress3 = fullAddress.substring(100);
					    }
	            
	             PerAddress1=(getAddress.getIsKra()==1?getAddress.getKraPerAddress1():PerAddress1!=null?PerAddress1:"");
				 PerAddress2=(getAddress.getIsKra()==1?getAddress.getKraPerAddress2():PerAddress2!=null?PerAddress2:"");
				 PerAddress3=(getAddress.getIsKra()==1?getAddress.getKraPerAddress3():PerAddress3!=null?PerAddress3:"");
				String State=(getAddress.getIsKra()==1?getAddress.getKraPerState():getAddress.getDigiPerState());
				String City=(getAddress.getIsKra()==1?getAddress.getKraPerCity():getAddress.getDigiPerLocality());
				System.out.println("the State"+State);
				String Pincode=(getAddress.getIsKra()==1?Integer.toString(getAddress.getKraPerPin()):getAddress.getDigiPerPincode());
	            //address
				String kraproof="";
				KraKeyValueEntity kraKeyValueEntityproof=null;
				if(getAddress.getIsKra()==1) {
					kraproof=getAddress.getKraaddressproof();
					kraKeyValueEntityproof=kraKeyValueRepository.findByMasterIdAndMasterNameAndKraValue("9","PROOF OF ADDRESS", kraproof);
				}
				
//				KraKeyValueEntity kraKeyValueEntityState = kraKeyValueRepository
//						.findByMasterIdAndMasterNameAndKraValue("1", "STATE", State);
//				String kraValuestate= kraKeyValueEntityState.getKraKey();
//
//				// Extract the last three characters
//				String kraValuestateSubstring = kraValuestate.substring(1);
				String kraValuestateSubstring = backOfficeRestService.getKRAStateCodeList(State);


				System.out.println("kraValuestateSubstring"+kraValuestateSubstring); // This will print "027" or the last three characters
	            jsonObject.addProperty("cAdd1", PerAddress1);
	            jsonObject.addProperty("cAdd2", PerAddress2);
	            jsonObject.addProperty("cAdd3",PerAddress3);
	            jsonObject.addProperty("cCity", City);
	            jsonObject.addProperty("cPin", Pincode);
	            //String stateCode = kraKeyValueEntityState.getKraKey();
	            String stateCode = backOfficeRestService.getStateCodeListonBO(State);
	            //System.out.println("the statecode11111"+stateCode);
	            jsonObject.addProperty("cStateCd", stateCode);
	            jsonObject.addProperty("cCountry", "India");
	            jsonObject.addProperty("cPhone", "");
	            jsonObject.addProperty("cFax", "");
	            //Residential Address
	            
	            jsonObject.addProperty("cResAdd1", PerAddress1);
	            jsonObject.addProperty("cResAdd2", PerAddress2);
	            jsonObject.addProperty("cResAdd3", PerAddress3);
	            jsonObject.addProperty("cResCity",City);
	            jsonObject.addProperty("cResPin",  Pincode);
	            jsonObject.addProperty("cResStateCd",stateCode);
	            
	            jsonObject.addProperty("cResCountry", "India");
	            jsonObject.addProperty("cResPhone", "");
	            jsonObject.addProperty("cResFax", "");
	            //Communication
	            jsonObject.addProperty("cEmail", userEntity.get().getEmailId());
	            jsonObject.addProperty("cMobile", userEntity.get().getMobileNo());
	            jsonObject.addProperty("FmlyEmailFlag", 0);
	            jsonObject.addProperty("FmlyMobileFlag", 0);
	            //KRA
	            jsonObject.addProperty("cKraApplDt",formattedDate);
	            jsonObject.addProperty("cKraOccupation", Kraoccupation);
	            jsonObject.addProperty("cKraOccupationDtlOth", "");
	            jsonObject.addProperty("cKraAnnualInc", kraincome);
	            jsonObject.addProperty("cKraAnnualIncDt", "");
	            
	            jsonObject.addProperty("cKraPEP", "");
	            jsonObject.addProperty("cKraCorrAddPrf","31");
	            jsonObject.addProperty("cKraCorrAddPrfId","");
	            jsonObject.addProperty("cKraCorrAddPrfDt", "");
	            jsonObject.addProperty("cKraPermAddPrf","31");
	            
	            jsonObject.addProperty("cKraPermAddPrfId", "");
	            jsonObject.addProperty("cKraPermAddPrfDt", "");
	            jsonObject.addProperty("cKraPermCorrSame", "Y");
	           
	            jsonObject.addProperty("cKraCorrAddState", kraValuestateSubstring);
	            jsonObject.addProperty("cKraPermAddState",kraValuestateSubstring);
	            jsonObject.addProperty("cKraCorrAddCntry", "101");
	            jsonObject.addProperty("cKraPermAddCntry", "101");
	            jsonObject.addProperty("cKraIdProof", kraKeyValueEntityproof!=null?kraKeyValueEntityproof.getKraKey():"");
	            String getKraCity=backOfficeRestService.getKRAStateCityList(getAddress.getKraCity()!=null?getAddress.getKraCity():getAddress.getDigiPerLocality());
	            if(getKraCity==null) {
	            	getKraCity=backOfficeRestService.getKRAStateCityList(getAddress.getDigiPerDistrict());
	            }
	            System.out.println("the getKraCity"+getKraCity);
	            jsonObject.addProperty("cKraStateCity",getKraCity);
	           // jsonObject.addProperty("cKraIpvDt","");
	         // Assuming ivr.getCreatedOn() returns a Date object
	            Date IpvDate=null;
	            if (ivr.getAttachementUrl() != null) {
	                IpvDate = ivr.getCreatedOn();
	                // Format the date as IPV Date (yyyy-MM-dd)
	                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	                String cKraIpvDt = sdf.format(IpvDate);
	                // Add cKraIpvDt to jsonObject
	                jsonObject.addProperty("cKraIpvDt", cKraIpvDt);
	            } else {
	                jsonObject.addProperty("cKraIpvDt", ""); // Set cKraIpvDt to an empty string
	            }
	            jsonObject.addProperty("cKraIpvDesig", "KYC EXECUTIVE");
	            jsonObject.addProperty("cKraIpvName", "YOGESWARI R");
	            jsonObject.addProperty("cKraIpvOrg", "SKY COMMODITIES");
	            jsonObject.addProperty("cKraIdProofOth", "");
	            jsonObject.addProperty("KRACompStatus", "");
	            
	            ReferralEntity referralEntity=referralRepository.findByMobileNo(userEntity.get().getMobileNo());
	            //Introducer Details
	            jsonObject.addProperty("cIntroClId","");
	            jsonObject.addProperty("cIntroName","GPKT");
	            jsonObject.addProperty("cIntroCtgry", "");
	            jsonObject.addProperty("cIntroCatDscr", "");
	            jsonObject.addProperty("cIntroAdd1", "");
	            
	            jsonObject.addProperty("cIntroAdd2", "");
	            jsonObject.addProperty("cIntroAdd3", "");
	            jsonObject.addProperty("cIntroCity", "");
	            jsonObject.addProperty("cIntroPin", "");
	            jsonObject.addProperty("cIntroCntry", "");
	            jsonObject.addProperty("cIntroPhone", "");
	            jsonObject.addProperty("cIntroFather", "");
	            jsonObject.addProperty("cIntroRel", "");
	            jsonObject.addProperty("cIntroIdPrf", "");
	            jsonObject.addProperty("cIntroId", referralEntity!=null? referralEntity.getReferralBy():"");
	            jsonObject.addProperty("cIntroIdExp","");
	            
	            //Contact Person 1
	            jsonObject.addProperty("cContactPerson","");
	            jsonObject.addProperty("cCpDesignation", "");
	            jsonObject.addProperty("cCpAddress","");
	            jsonObject.addProperty("cCpPhone","");
	            jsonObject.addProperty("cCpCity", "");
	            jsonObject.addProperty("cCpPin", "");
	            jsonObject.addProperty("cCpState","");
	            jsonObject.addProperty("cCpCountry","");
	            //Primary Bank Details
	            jsonObject.addProperty("cIfsc",getBankDetails.getIfsc()!=null? getBankDetails.getIfsc():"");
	            jsonObject.addProperty("cMicr",getBankDetails.getMicr());
	            jsonObject.addProperty("cAcType", "Savings");
	            jsonObject.addProperty("cAcNo", getBankDetails.getAccountNo());
	            jsonObject.addProperty("cBankClientName",  userEntity.get().getUserName().substring(0, Math.min(userEntity.get().getUserName().length(), 39)));
	            
	            //Secondary Bank Details
	            jsonObject.addProperty("cSecIfsc", "");
	            jsonObject.addProperty("cSecMicr", "");
	            jsonObject.addProperty("cSecAcType", "");
	            jsonObject.addProperty("cSecAcNo", "");
	            jsonObject.addProperty("cSecBankClientName", "");
	            jsonObject.addProperty("PayMode", "");
	            jsonObject.addProperty("cAutoFundPo", "");
	            jsonObject.addProperty("cClientType", "");
	            jsonObject.addProperty("cDealMode", "");
	            jsonObject.addProperty("cRegFrmRecvd", "");
	            jsonObject.addProperty("cIpvFlag",1);
	            
	            SegmentEntity SegmentEntity=segmentRepository.findByapplicationId(applicationId);
	            //segment 
	            jsonObject.addProperty("cREG_NSE",SegmentEntity!=null? SegmentEntity.getEquCash():0);
	            jsonObject.addProperty("cREG_NFO",SegmentEntity!=null? SegmentEntity.getEd():0);
	            jsonObject.addProperty("cREG_BSE", "");
	            
	            jsonObject.addProperty("cREG_BFO","");
	            jsonObject.addProperty("cREG_CSE", "");
	            jsonObject.addProperty("cREG_DSE", "");
	            jsonObject.addProperty("cREG_NCDEX", "");
	            jsonObject.addProperty("cREG_MCX", SegmentEntity!=null? SegmentEntity.getComm():0);
	            jsonObject.addProperty("cREG_MF", "");
	            jsonObject.addProperty("cREG_NBFC", "");
	            jsonObject.addProperty("cREG_DGCX", "");
	            jsonObject.addProperty("cREG_NMCEIL","");
	            jsonObject.addProperty("cREG_CNFO", SegmentEntity!=null? SegmentEntity.getCd():0);
	            jsonObject.addProperty("cREG_CMFO", "");
	            
	            jsonObject.addProperty("cREG_CBFO", "");
	            jsonObject.addProperty("cREG_PMS", "");
	            jsonObject.addProperty("cREG_ICFO", "");
	            jsonObject.addProperty("cREG_NMFS", "");
	            jsonObject.addProperty("cREG_NSFO", "");
	            jsonObject.addProperty("cREG_KCFO", "");
	            jsonObject.addProperty("cREG_USFO", "");
	            jsonObject.addProperty("cREG_CSENSE", "");
	            jsonObject.addProperty("cREG_CSENFO", "");
	            jsonObject.addProperty("cREG_MXEQ", "");
	            jsonObject.addProperty("cREG_MXFO", "");
	            
	            
	            jsonObject.addProperty("cREG_UCX", "");
	            jsonObject.addProperty("cREG_NSEDS","");
	            jsonObject.addProperty("cREG_BMFS", "");
	            jsonObject.addProperty("cREG_NSLB", "");
	            jsonObject.addProperty("cREG_NSPT","");
	            jsonObject.addProperty("IBTActive", "");
	            jsonObject.addProperty("FATCAReceivedDate", "");
	            jsonObject.addProperty("cHoldDlvry","");
	            //dp details
	            
	            jsonObject.addProperty("cDpId", "");
	            jsonObject.addProperty("cBenAcNum","");
	            //nominee opt out 
	            jsonObject.addProperty("NomOptOutDt", "");
	            
				List<NomineeEntity> nomineeEntity = nomineeRepository.findByapplicationId(applicationId);
				
				if (!nomineeEntity.isEmpty()) {
					for (int i = 0; i < nomineeEntity.size(); i++) {
						if (i == 0) {
							jsonObject.addProperty("Nominee1_EffectDate",formattedDate);
							jsonObject.addProperty("Nominee1_Descr", nomineeEntity.get(i).getFirstname()+" "+nomineeEntity.get(i).getLastname());
							jsonObject.addProperty("Nominee1_Percent",nomineeEntity.get(i).getAllocation());
							jsonObject.addProperty("Nominee1_Relation",nomineeEntity.get(i).getRelationship());
							jsonObject.addProperty("Nominee1_Add1",nomineeEntity.get(i).getAddress1());
							jsonObject.addProperty("Nominee1_Add2",nomineeEntity.get(i).getAddress2());
							//jsonObject.addProperty("Nominee1_City",nomineeEntity.get(i).get);
							jsonObject.addProperty("Nominee1_Pincode",nomineeEntity.get(i).getPincode());
							jsonObject.addProperty("Nominee1_State",stateCode);
							jsonObject.addProperty("Nominee1_Country", "IN");
							jsonObject.addProperty("Nominee1_Mobile", nomineeEntity.get(i).getMobilenumber());
							jsonObject.addProperty("Nominee1_Email",nomineeEntity.get(i).getEmailaddress());
							jsonObject.addProperty("Nominee1_IdType",getProofTypeCode(nomineeEntity.get(i).getTypeOfProof()));
							jsonObject.addProperty("Nominee1_IdNumber",nomineeEntity.get(i).getProofId());
							GuardianEntity guardianEntity = guardianRepository
									.findByNomineeId(nomineeEntity.get(i).getId());
							if (guardianEntity != null) {
								jsonObject.addProperty("Nominee1_IsMinor", "1");
								jsonObject.addProperty("Nominee1_MinorDOB", parseAndFormatDate(guardianEntity.getDateOfbirth()));
								jsonObject.addProperty("Nominee1_GdDescr",guardianEntity.getFirstname()+" "+guardianEntity.getLastname());
								jsonObject.addProperty("Nominee1_GdRelation",guardianEntity.getRelationship());
								jsonObject.addProperty("Nominee1_GdAdd1",guardianEntity.getAddress1());
								jsonObject.addProperty("Nominee1_GdAdd2",guardianEntity.getAddress2());
								//jsonObject.addProperty("Nominee1_GdCity",guardianEntity.ge);
								jsonObject.addProperty("Nominee1_GdPincode",guardianEntity.getPincode());
								jsonObject.addProperty("Nominee1_GdState",stateCode);
								jsonObject.addProperty("Nominee1_GdCountry","IN");
								jsonObject.addProperty("Nominee1_GdMobile",guardianEntity.getMobilenumber());
								jsonObject.addProperty("Nominee1_GdEmail",guardianEntity.getEmailaddress());
								jsonObject.addProperty("Nominee1_GdIdType",getProofTypeCode(guardianEntity.getTypeOfProof()));
								jsonObject.addProperty("Nominee1_GdIdNumber",guardianEntity.getProofId());
							}else {
								jsonObject.addProperty("Nominee1_IsMinor", "0");	
							}
						} else if (i == 1) {
							jsonObject.addProperty("Nominee2_EffectDate",formattedDate.toString());
							jsonObject.addProperty("Nominee2_Descr", nomineeEntity.get(i).getFirstname()+" "+nomineeEntity.get(i).getLastname());
							jsonObject.addProperty("Nominee2_Percent",nomineeEntity.get(i).getAllocation());
							jsonObject.addProperty("Nominee2_Relation",nomineeEntity.get(i).getRelationship());
							jsonObject.addProperty("Nominee2_Add1",nomineeEntity.get(i).getAddress1());
							jsonObject.addProperty("Nominee2_Add2",nomineeEntity.get(i).getAddress2());
							//jsonObject.addProperty("Nominee1_City",nomineeEntity.get(i).get);
							jsonObject.addProperty("Nominee2_Pincode",nomineeEntity.get(i).getPincode());
							jsonObject.addProperty("Nominee2_State",stateCode);
							jsonObject.addProperty("Nominee2_Country", "IN");
							jsonObject.addProperty("Nominee2_Mobile", nomineeEntity.get(i).getMobilenumber());
							jsonObject.addProperty("Nominee2_Email",nomineeEntity.get(i).getEmailaddress());
							jsonObject.addProperty("Nominee2_IdType",getProofTypeCode(nomineeEntity.get(i).getTypeOfProof()));
							jsonObject.addProperty("Nominee2_IdNumber",nomineeEntity.get(i).getProofId());
							GuardianEntity guardianEntity = guardianRepository
									.findByNomineeId(nomineeEntity.get(i).getId());
							if (guardianEntity != null) {
								jsonObject.addProperty("Nominee2_IsMinor", "1");
								jsonObject.addProperty("Nominee2_MinorDOB",parseAndFormatDate(guardianEntity.getDateOfbirth()));
								jsonObject.addProperty("Nominee2_GdDescr",guardianEntity.getFirstname()+" "+guardianEntity.getLastname());
								jsonObject.addProperty("Nominee2_GdRelation",guardianEntity.getRelationship());
								jsonObject.addProperty("Nominee2_GdAdd1",guardianEntity.getAddress1());
								jsonObject.addProperty("Nominee2_GdAdd2",guardianEntity.getAddress2());
								//jsonObject.addProperty("Nominee1_GdCity",guardianEntity.ge);
								jsonObject.addProperty("Nominee2_GdPincode",guardianEntity.getPincode());
								jsonObject.addProperty("Nominee2_GdState", stateCode);
								jsonObject.addProperty("Nominee2_GdCountry","IN");
								jsonObject.addProperty("Nominee2_GdMobile",guardianEntity.getMobilenumber());
								jsonObject.addProperty("Nominee2_GdEmail",guardianEntity.getEmailaddress());
								jsonObject.addProperty("Nominee2_GdIdType",getProofTypeCode(guardianEntity.getTypeOfProof()));
								jsonObject.addProperty("Nominee2_GdIdNumber",guardianEntity.getProofId());
							}else {
								jsonObject.addProperty("Nominee2_IsMinor", "0");	
							}}
						 else if (i == 2) {
							 jsonObject.addProperty("Nominee3_EffectDate",formattedDate.toString());
								jsonObject.addProperty("Nominee3_Descr", nomineeEntity.get(i).getFirstname()+" "+nomineeEntity.get(i).getLastname());
								jsonObject.addProperty("Nominee3_Percent",nomineeEntity.get(i).getAllocation());
								jsonObject.addProperty("Nominee3_Relation",nomineeEntity.get(i).getRelationship());
								jsonObject.addProperty("Nominee3_Add1",nomineeEntity.get(i).getAddress1());
								jsonObject.addProperty("Nominee3_Add2",nomineeEntity.get(i).getAddress2());
								//jsonObject.addProperty("Nominee1_City",nomineeEntity.get(i).get);
								jsonObject.addProperty("Nominee3_Pincode",nomineeEntity.get(i).getPincode());
								jsonObject.addProperty("Nominee3_State",stateCode);
								jsonObject.addProperty("Nominee3_Country", "IN");
								jsonObject.addProperty("Nominee3_Mobile", nomineeEntity.get(i).getMobilenumber());
								jsonObject.addProperty("Nominee3_Email",nomineeEntity.get(i).getEmailaddress());
								jsonObject.addProperty("Nominee3_IdType",getProofTypeCode(nomineeEntity.get(i).getTypeOfProof()));
								jsonObject.addProperty("Nominee3_IdNumber",nomineeEntity.get(i).getProofId());
								GuardianEntity guardianEntity = guardianRepository
										.findByNomineeId(nomineeEntity.get(i).getId());
								if (guardianEntity != null) {
									jsonObject.addProperty("Nominee3_IsMinor", "1");
									jsonObject.addProperty("Nominee3_MinorDOB",parseAndFormatDate(guardianEntity.getDateOfbirth()));
									jsonObject.addProperty("Nominee3_GdDescr",guardianEntity.getFirstname()+" "+guardianEntity.getLastname());
									jsonObject.addProperty("Nominee3_GdRelation",guardianEntity.getRelationship());
									jsonObject.addProperty("Nominee3_GdAdd1",guardianEntity.getAddress1());
									jsonObject.addProperty("Nominee3_GdAdd2",guardianEntity.getAddress2());
									//jsonObject.addProperty("Nominee1_GdCity",guardianEntity.ge);
									jsonObject.addProperty("Nominee3_GdPincode",guardianEntity.getPincode());
									jsonObject.addProperty("Nominee3_GdState", stateCode);
									jsonObject.addProperty("Nominee3_GdCountry","IN");
									jsonObject.addProperty("Nominee3_GdMobile",guardianEntity.getMobilenumber());
									jsonObject.addProperty("Nominee3_GdEmail",guardianEntity.getEmailaddress());
									
									jsonObject.addProperty("Nominee3_GdIdType",getProofTypeCode(guardianEntity.getTypeOfProof()));
									jsonObject.addProperty("Nominee3_GdIdNumber",guardianEntity.getProofId());
								}else {
									jsonObject.addProperty("Nominee3_IsMinor", "0");	
								}
						}}}
	            
	            //brokerage
	            
	            jsonObject.addProperty("cBrkgBasketNSE",  "");
	            jsonObject.addProperty("cBrkgBasketBSE",  "");
	            jsonObject.addProperty("cBrkgBasketFO",  "");
	            jsonObject.addProperty("cBrkgBasketCDS",  "");
	            jsonObject.addProperty("cBrkgBasketCOMM",  "");
	            jsonObject.addProperty("cBrkgBasketBSEFO",  "");
	            jsonObject.addProperty("cBrkgBasketBSECDS",  "");
//	            ZonedDateTime currentDateTime = ZonedDateTime.now(ZoneOffset.UTC);
//	            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
//	            String formattedDateTime = currentDateTime.format(formatter);
	            jsonObject.addProperty("dBrkgEffectDate", "");
	            jsonObject.addProperty("nDelCMBrkg",  "");
	            jsonObject.addProperty("nDelCMBrkgMin", "");
	            jsonObject.addProperty("nSqCMBrkg",  "");
	            
	            
	            jsonObject.addProperty("nSqCMBrkgMin", "");
	            jsonObject.addProperty("nFutBrkg",  "");
	            jsonObject.addProperty("nFutBrkgMin",  "");
	            jsonObject.addProperty("nOptBrkg",  ""); 
	            //running acc 
	          
	            jsonObject.addProperty("cRunAcSegment","Both");
	            jsonObject.addProperty("dRunAcRcvdDt",formattedDate);
	            jsonObject.addProperty("dRunAcRvkdDt", "");
	            jsonObject.addProperty("nSettleType",2);
	            jsonObject.addProperty("Retainable", "");
	            
	            //comm
	            jsonObject.addProperty("dRunAcRcvdDtCom", formattedDate);
	            jsonObject.addProperty("dRunAcRvkdDtCom", "");
	            jsonObject.addProperty("nSettleTypeCom",2);
	            jsonObject.addProperty("RetainableCom",  "");
	            jsonObject.addProperty("BusiCommenceDt",formattedDate);
	            
	            
	            
	            jsonObject.addProperty("cGroupCd",  "");
	            jsonObject.addProperty("dAPEffectDate", "");
	            jsonObject.addProperty("cAPCd",  "");
	            jsonObject.addProperty("dRMEffectDate", "");
	            
	            jsonObject.addProperty("cRMCd",  "");
	            jsonObject.addProperty("dDealerEffectDate",  "");
	            jsonObject.addProperty("cDealerCd",  "");
	            jsonObject.addProperty("dSREffectDate",  "");
	            
	            jsonObject.addProperty("cClientWithOthTM",  "");
	            jsonObject.addProperty("cSRCd",  "");
	            jsonObject.addProperty("eContract",  "1");
	         // Convert the JSON object to a string
	             jsonData = jsonObject.toString();
	        }
		return jsonData;
	}
	private String parseAndFormatDate(String dateStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date date = inputFormat.parse(dateStr);
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return ""; // Handle the exception appropriately
        }
    }
	private String getProofTypeCode(String proofType) {
		String NomineeType=proofType;
		String NomineeTypeCode=null;
		if(NomineeType!=null) {
		if (NomineeType.contains("Pan")) {
			NomineeTypeCode="C";
		}else if (NomineeType.contains("Aadhar card")) {
			NomineeTypeCode="E";
		}else if (NomineeType.contains("Driving licence")) {
			NomineeTypeCode="D";
		}else if (NomineeType.contains("Voter ID")) {
			NomineeTypeCode="B";
		}else if (NomineeType.contains("Passport")) {
			NomineeTypeCode="A";
		}}
		return NomineeTypeCode;
    }

	
}