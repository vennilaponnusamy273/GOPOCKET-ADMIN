package in.codifi.api.request.model;
import javax.persistence.Column;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiStatusModel {
	
	 	@Column(name = "application_id")
	    private Long applicationId;

	    @Column(name = "stage")
	    private String stage;

	    @Column(name = "status")
	    private Integer status;

	    @Column(name = "approved_by")
	    private String approvedBy;

	    @Column(name = "reason")
	    private String reason;
	    
	    @Column(name = "docType")
	    private String docType;
	    
	    @Column(name = "nomineeid")
		private Long nomineeId;
}
