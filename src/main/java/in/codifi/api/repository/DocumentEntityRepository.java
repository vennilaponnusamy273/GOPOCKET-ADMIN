package in.codifi.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import in.codifi.api.entity.DocumentEntity;

public interface DocumentEntityRepository extends JpaRepository<DocumentEntity, Long> {

	DocumentEntity findByApplicationIdAndDocumentType(long applicationId, String documentType);	

}
