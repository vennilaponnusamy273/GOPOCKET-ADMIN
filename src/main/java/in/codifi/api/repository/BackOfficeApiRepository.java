package in.codifi.api.repository;

import org.springframework.data.repository.CrudRepository;

import in.codifi.api.entity.BackOfficeApiEntity;

public interface BackOfficeApiRepository extends CrudRepository<BackOfficeApiEntity, Long> {

	BackOfficeApiEntity findByapplicationId(Long applicationId);
}
