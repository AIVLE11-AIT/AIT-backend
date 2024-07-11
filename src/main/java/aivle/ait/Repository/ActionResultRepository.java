package aivle.ait.Repository;

import aivle.ait.Entity.ActionResult;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ActionResultRepository extends CrudRepository<ActionResult, Long> {
    @Modifying
    @Query("UPDATE ActionResult c SET c.result = null WHERE c.result.id = :resultId")
    void detachActionResultFromResult(@Param("resultId") Long resultId);
}
