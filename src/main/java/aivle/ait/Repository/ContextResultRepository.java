package aivle.ait.Repository;

import aivle.ait.Entity.ContextResult;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ContextResultRepository extends CrudRepository<ContextResult, Long> {
    @Modifying
    @Query("UPDATE ContextResult c SET c.result = null WHERE c.result.id = :resultId")
    void detachContextResultFromResult(@Param("resultId") Long resultId);
}
