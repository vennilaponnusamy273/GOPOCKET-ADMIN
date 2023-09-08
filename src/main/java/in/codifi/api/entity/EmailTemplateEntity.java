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
@Entity(name = "tbl_email_template")
public class EmailTemplateEntity extends CommonEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Lob
	@Column(name = "body")
	private String body;

	@Column(name = "subject")
	private String subject;

	@Column(name = "keyData") // escaping "key" with backticks
	private String keyData;

	@Column(name = "value")
	private Long value;
	
	@Column(name = "cc")
	private String cc;
	
	@Column(name = "toAddress")
	private String toAddress;
	
	@Column(name = "Bcc")
	private String bcc;
}
