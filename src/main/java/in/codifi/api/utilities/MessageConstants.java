package in.codifi.api.utilities;

public class MessageConstants {

	// RESPONSE MODULE CLASS
	public static final int FAILED_STATUS = 0;
	public static final int SUCCESS_STATUS = 1;
	public static final String FAILED_MSG = "Failed";
	public static final String SUCCESS_MSG = "Success";
	public static final String START_TIME = " 00:00:00";
	public static final String END_TIME = " 23:59:59";

	// GETUSERDETAILS
	public static final Object OFFSETEXIT = "The OffSet Exits the data";
	public static final String LIMIT_VALIDATION_MESSAGE = "Limit should be less than 50";
	public static final String NO_RECORD_FOUND = "No Record found";

	// DOCUMENT DOWNLOAD
	public static final String FILE_NOT_FOUND = "File Not found on this APPLICATION_ID";
	public static final String DOC_ERROR = "While creating Error for  Document";
	public static final String DOC_NOT_FOUND = "Document is not found";
	public static final String FILE_DOWNLOAD_SUCCESS_MSG = "File Downloaded successfully, Check Download Directory";

	public static final String UBUNTU_FILE_SEPERATOR = "/";
	public static final String WINDOWS_FILE_SEPERATOR = "\\\\";
	public static final String OS_WINDOWS = "win";

	public static final String PARAMETER_NULL = "The Given Parameter is null";

	public static final String DOC_APPROVED = "DOCUMENT APPROVED SUCCESSFULLY";
	public static final String DOC_REJECTED = "DOCUMENT REJECTED SUCCESSFULLY";
	public static final String ERROR_WHILE_SAVING_DOCSTATUS = "Error Occur form saving Document status";
	public static final String NOT_DOC_AVAILABLE = "Document Details Not Available in User Document  Details";
	public static final String INVLAID_PARAMETER = "The Given Parameter is Invalid";

	public static final String USER_ID_NULL = "The given user Id is null";
	public static final String STATUSINVALID = "Invalid status value. Only approved - 1 or rejected - 0 are allowed.";
	public static final String STATUSNOTFOUND = "Status not found for the given application and stage.";
	public static final String STAGENOTFOUND = "Given Stage is Invaild";
	public static final String USER_ID_INVALID = "The given user Id is Invalid";
	public static final String IFSC_INVALID = "The given user IFSC Code is Invalid";

	public static final String REFERRAL_BY_NULL = "Referral by is null";
	public static final String MOBILE_NO_NULL = "Mobile Number is null";
	public static final String EKYC_URL = "https://ekyc.gopocket.in/?refBy=";
	public static final String FAILED_HTTP_CODE = "Failed : HTTP error code :";
}
