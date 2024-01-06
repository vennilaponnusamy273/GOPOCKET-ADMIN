package in.codifi.api.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import in.codifi.api.entity.ApiStatusEntity;

public interface ApiStatusRepository extends CrudRepository<ApiStatusEntity, Long> {

	List<ApiStatusEntity> findByApplicationId(Long applicationId);
	List<ApiStatusEntity> findByApplicationIdAndStatus(Long applicationId, Integer status);
	ApiStatusEntity findByApplicationIdAndStage(Long applicationId, String stage);
	ApiStatusEntity findByApplicationIdAndStageAndDocType(Long applicationId, String stage,String docType);
	ApiStatusEntity findByApplicationIdAndStageAndNomineeId(Long applicationId, String stage,Long nomineeId);
}
