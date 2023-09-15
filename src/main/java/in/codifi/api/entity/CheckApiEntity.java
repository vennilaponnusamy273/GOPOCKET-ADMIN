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
@Entity(name = "tbl_check_api")
public class CheckApiEntity extends CommonEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "application_id")
	private Long applicationId;

	@Column(name = "start_status")
	private Long startStatus=0L;

	@Column(name = "form_code")
	private String formCode;

	@Column(name = "ld")
	private int ldCompleted;

	@Column(name = "nse")
	private int nseCompleted;

	@Column(name = "bse")
	private int bseCompleted;

	@Column(name = "kra")
	private int kraCompleted;

	@Column(name = "digi")
	private int digiCompleted;

	@Column(name = "kra_image")
	private int kraImageCompleted;

	@Column(name = "bse_star_mf")
	private int mfCompleted;

	@Column(name = "bse_star_mf_fatca")
	private int mfFatcaCompleted;

	@Column(name = "bse_star_mf_aof")
	private int mfAOFCompleted;

	@Column(name = "iwapp")
	private int iwappCompleted;

	@Column(name = "ckyc")
	private int ckycCompleted;

	@Column(name = "rejection_mail")
	private int rejectionMail;
}
