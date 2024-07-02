package aivle.ait.Repository;

import aivle.ait.Entity.ActionResult;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ActionResultRepository extends CrudRepository<ActionResult, Long> {
    Optional<ActionResult> findByFileId(Long file_id);
}
