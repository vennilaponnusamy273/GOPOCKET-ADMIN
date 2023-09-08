package in.codifi.api.repository;

import org.springframework.data.repository.CrudRepository;

import in.codifi.api.entity.SegmentEntity;

public interface SegmentRepository extends CrudRepository<SegmentEntity, Long> {

	SegmentEntity findByapplicationId(Long applicationId);
	
	void deleteByApplicationId(long applicationId);

}
