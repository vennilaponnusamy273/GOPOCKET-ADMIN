package in.codifi.api.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import in.codifi.api.entity.NomineeEntity;

public interface NomineeRepository extends CrudRepository<NomineeEntity, Long> {

	Long countByApplicationId(Long applicationId);

	List<NomineeEntity> findByapplicationId(Long applicationId);
	
	void deleteByApplicationId(long applicationId);

}
