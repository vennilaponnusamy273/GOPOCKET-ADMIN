package in.codifi.api.helper;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gson.JsonObject;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.AddressEntity;
import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.entity.BankEntity;
import in.codifi.api.entity.IvrEntity;
import in.codifi.api.entity.KraKeyValueEntity;
import in.codifi.api.entity.PaymentEntity;
import in.codifi.api.entity.ProfileEntity;
import in.codifi.api.entity.ReferralEntity;
import in.codifi.api.entity.ResponseCkyc;
import in.codifi.api.entity.SegmentEntity;
import in.codifi.api.repository.AddressRepository;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.repository.BankRepository;
import in.codifi.api.repository.CkycResponseRepos;
import in.codifi.api.repository.IvrRepository;
import in.codifi.api.repository.KraKeyValueRepository;
import in.codifi.api.repository.PaymentRepository;
import in.codifi.api.repository.ProfileRepository;
import in.codifi.api.repository.ReferralRepository;
import in.codifi.api.repository.SegmentRepository;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.StringUtil;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
	
	public  String generateJsonContenet(long  applicationId ) throws ParseException {
		 String jsonData = "";
		   Optional<ApplicationUserEntity> userEntity = applicationUserRepository.findById(applicationId);
	        ProfileEntity getProfile = profileRepository.findByapplicationId(applicationId);
	        BankEntity getBankDetails = bankRepository.findByapplicationId(applicationId);
	        AddressEntity getAddress = addressRepository.findByapplicationId(applicationId);
	        ResponseCkyc responseCkyc=ckycResponseRepos.findByApplicationId(applicationId);
	        IvrEntity ivr=ivrRepository.findByApplicationId(applicationId);
	        PaymentEntity paymentEntity=paymentRepository.findByApplicationId(applicationId);
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
				  if (StringUtil.isEqual(getProfile.getAnnualIncome(), "0-1 lakh")) {
					  income = "01";
					} else if (StringUtil.isEqual(getProfile.getAnnualIncome(), "1-5 lakhs")) {
						income = "02";				
					} else if (StringUtil.isEqual(getProfile.getAnnualIncome(), "5-10 lakhs")) {
						income = "03";
					} else if (StringUtil.isEqual(getProfile.getAnnualIncome(), "10-20 lakhs")) {
						income = "04";
					} else if (StringUtil.isEqual(getProfile.getAnnualIncome(), "More than 20 lakhs")) {
						income = "06";
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
	            jsonObject.addProperty("cAadhaar", getAddress.getAadharNo()!=null?getAddress.getAadharNo():"");
	            jsonObject.addProperty("cPhotoIdPrf", "");
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
	            jsonObject.addProperty("cAddPrf", kraAddressProof != null ? kraAddressProof : "ADHAAR");
	            StringBuilder addressBuilder = new StringBuilder();
	            if (getAddress != null) {
				    if (getAddress.getFlatNo() != null) {
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
				    }
				    if (getAddress.getAddress1() != null) {
				        if (addressBuilder.length() > 0) {
				            addressBuilder.append(" ");
				        }
				        addressBuilder.append(getAddress.getAddress1());
				    }}
				    String fullAddress = addressBuilder.toString();
	            
	            String PerAddress1=(getAddress.getIsKra()==1?getAddress.getKraPerAddress1():fullAddress);
				String PerAddress2=(getAddress.getIsKra()==1?getAddress.getKraPerAddress2():getAddress.getAddress2());
				String PerAddress3=(getAddress.getIsKra()==1?getAddress.getKraPerAddress3():getAddress.getAddress2());
				String State=(getAddress.getIsKra()==1?getAddress.getKraPerState():getAddress.getState());
				String City=(getAddress.getIsKra()==1?getAddress.getKraPerCity():getAddress.getLandmark());
				//String dist=(getAddress.getIsKra()==1?getAddress.getKraPerCity():getAddress.getDistrict());
				String Pincode=(getAddress.getIsKra()==1?Integer.toString(getAddress.getKraPerPin()):getAddress.getPincode().toString());
	            //address
				String kraproof="";
				KraKeyValueEntity kraKeyValueEntityproof=null;
				if(getAddress.getIsKra()==1) {
					kraproof=getAddress.getKraaddressproof();
					kraKeyValueEntityproof=kraKeyValueRepository.findByMasterIdAndMasterNameAndKraValue("PROOF OF ADDRESS","9", kraproof);
				}
				 KraKeyValueEntity kraKeyValueEntityState=kraKeyValueRepository.findByMasterIdAndMasterNameAndKraValue("STATE","1", State);
	            jsonObject.addProperty("cAdd1", PerAddress1);
	            jsonObject.addProperty("cAdd2", PerAddress2);
	            jsonObject.addProperty("cAdd3",PerAddress3);
	            jsonObject.addProperty("cCity", City);
	            jsonObject.addProperty("cPin", Pincode);
	            jsonObject.addProperty("cStateCd", kraKeyValueEntityState!=null?kraKeyValueEntityState.getKraKey():"");
	            jsonObject.addProperty("cCountry", "India");
	            jsonObject.addProperty("cPhone", "");
	            jsonObject.addProperty("cFax", "");
	            //Residential Address
	            
	            jsonObject.addProperty("cResAdd1", PerAddress1);
	            jsonObject.addProperty("cResAdd2", PerAddress2);
	            jsonObject.addProperty("cResAdd3", PerAddress3);
	            jsonObject.addProperty("cResCity",City);
	            jsonObject.addProperty("cResPin",  Pincode);
	            jsonObject.addProperty("cResStateCd",kraKeyValueEntityState!=null?kraKeyValueEntityState.getKraKey():"");
	            
	            jsonObject.addProperty("cResCountry", "India");
	            jsonObject.addProperty("cResPhone", "");
	            jsonObject.addProperty("cResFax", "");
	            //Communication
	            jsonObject.addProperty("cEmail", userEntity.get().getEmailId());
	            jsonObject.addProperty("cMobile", userEntity.get().getMobileNo());
	            jsonObject.addProperty("FmlyEmailFlag", 0);
	            jsonObject.addProperty("FmlyMobileFlag", 0);
	            //KRA
	            jsonObject.addProperty("cKraApplDt", "");
	            jsonObject.addProperty("cKraOccupation", Kraoccupation);
	            jsonObject.addProperty("cKraOccupationDtlOth", "");
	            jsonObject.addProperty("cKraAnnualInc", income);
	            jsonObject.addProperty("cKraAnnualIncDt", "");
	            
	            jsonObject.addProperty("cKraPEP", "");
	            jsonObject.addProperty("cKraCorrAddPrf",kraKeyValueEntityproof!=null?kraKeyValueEntityproof.getKraKey():"");
	            jsonObject.addProperty("cKraCorrAddPrfId", getAddress.getKraproofIdNumber()!=null?getAddress.getKraproofIdNumber():"");
	            jsonObject.addProperty("cKraCorrAddPrfDt", "");
	            jsonObject.addProperty("cKraPermAddPrf", kraKeyValueEntityproof!=null?kraKeyValueEntityproof.getKraKey():"");
	            
	            jsonObject.addProperty("cKraPermAddPrfId",  getAddress.getKraproofIdNumber()!=null?getAddress.getKraproofIdNumber():"");
	            jsonObject.addProperty("cKraPermAddPrfDt", "");
	            jsonObject.addProperty("cKraPermCorrSame", "N");
	           
	            jsonObject.addProperty("cKraCorrAddState",kraKeyValueEntityState!=null?kraKeyValueEntityState.getKraKey():"");
	            jsonObject.addProperty("cKraPermAddState",kraKeyValueEntityState!=null?kraKeyValueEntityState.getKraKey():"");
	            jsonObject.addProperty("cKraCorrAddCntry", "01");
	            jsonObject.addProperty("cKraPermAddCntry", "01");
	            jsonObject.addProperty("cKraIdProof", kraKeyValueEntityproof!=null?kraKeyValueEntityproof.getKraKey():"");
	            String getKraCity=callBCCity(City);
	            System.out.println("the getKraCity"+getKraCity);
	            jsonObject.addProperty("cKraStateCity","");
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
	            jsonObject.addProperty("cIntroClId",referralEntity!=null? referralEntity.getReferralBy():"");
	            jsonObject.addProperty("cIntroName", referralEntity!=null?  referralEntity.getRefByName():"");
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
	          
	            jsonObject.addProperty("cRunAcSegment","Securities");
	            jsonObject.addProperty("dRunAcRcvdDt",  "");
	            jsonObject.addProperty("dRunAcRvkdDt", "");
	            jsonObject.addProperty("nSettleType",  "");
	            jsonObject.addProperty("Retainable", "");
	            
	            //comm
	            jsonObject.addProperty("dRunAcRcvdDtCom", "");
	            jsonObject.addProperty("dRunAcRvkdDtCom", "");
	            jsonObject.addProperty("nSettleTypeCom",  "");
	            jsonObject.addProperty("RetainableCom",  "");
	            
	            
	            
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
	         // Convert the JSON object to a string
	             jsonData = jsonObject.toString();
	        }
		return jsonData;
	}

	public String callBCCity(String city) {
	    OkHttpClient client = new OkHttpClient();

	    // Define the URL
	    String apiUrl = "https://bo.skybroking.com/shrdbms/dotnet/api/stansoft/getKRAStateCityList";

	    // Create the JSON request body
	    String json = "{\"key\": \"ezM0OTM4Q0Y1LUIyNUItNDhFMi1CNEU2LTRDQkY5MjhGQjE2M30=\"";

	    if (city != null && !city.isEmpty()) {
	        json += ", \"cSearch\": \"" + city + "\"";
	    }

	    json += "}";

	    // Create a request
	    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
	    Request request = new Request.Builder()
	            .url(apiUrl)
	            .post(requestBody) // Use POST method as required
	            .build();

	    try (Response response = client.newCall(request).execute()) {
	        if (response.isSuccessful()) {
	            String responseBody = response.body().string();
	           // System.out.println("the " + responseBody);
	            return responseBody;
	        } else {
	            // Handle the error response here (e.g., log or throw an exception)
	            return "Error: " + response.code();
	        }
	    } catch (IOException e) {
	        // Handle network or other IO exceptions here
	        return "Error: " + e.getMessage();
	    }
	}
}