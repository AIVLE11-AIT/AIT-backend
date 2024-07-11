package aivle.ait.Repository;

import aivle.ait.Entity.VoiceResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoiceResultRepository extends JpaRepository<VoiceResult, Long> {
    @Modifying
    @Query("UPDATE VoiceResult c SET c.result = null WHERE c.result.id = :resultId")
    void detachVoiceResultFromResult(@Param("resultId") Long resultId);
}
