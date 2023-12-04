package in.codifi.api.entity;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity(name = "tbl_document_details")
public class DocumentEntity  extends CommonEntity implements Serializable {

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

	@Column(name = "attachement")
	private String attachement;

	@Column(name = "attachement_url")
	private String attachementUrl;

	@Column(name = "type_of_proof")
	private String typeOfProof;

	@Column(name = "document_type")
	private String documentType;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(name = "password")
	private String password;
	
	@Column(name = "isApproval")
	private int isApproval;
	
	@Column(name = "isRejected")
	private int isRejected;

}
