package in.codifi.api.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "tbl_api_status")
public class ApiStatusEntity extends CommonEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "application_id")
	private Long applicationId;

	@Column(name = "stage")
	private String stage;

	@Column(name = "status")
	private Integer status;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(name = "approved_on")
	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date approvedOn;

	@Column(name = "approved_by")
	private String approvedBy;

	@Column(name = "reason")
	private String reason;

	@Column(name = "docType")
	private String docType;
	
	@Column(name = "nomineeid")
	private Long nomineeId;

}
