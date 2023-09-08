package in.codifi.api.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "tbl_backoffice_entity")
public class BackOfficeApiEntity extends CommonEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	public  Long id;

	@Column(name = "application_id")
	public Long applicationId;
	
	@Lob
	@Column(name = "req")
	public String req;

	@Lob
	@Column(name = "res")
	public String res;
	
	@Lob
	@Column(name = "jsondata")
	public String jsonData;
}
