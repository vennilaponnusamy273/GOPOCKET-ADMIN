package in.codifi.api.repository;

import org.springframework.data.repository.CrudRepository;

import in.codifi.api.entity.GuardianEntity;

public interface GuardianRepository extends CrudRepository<GuardianEntity, Long> {

	GuardianEntity findByNomineeId(Long nomineeId);
	
	void deleteByApplicationId(long applicationId);

}
