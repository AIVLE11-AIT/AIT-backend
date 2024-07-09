package aivle.ait.Repository;

import aivle.ait.Entity.IntroductionVideo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IntroductionVideoRepository extends JpaRepository<IntroductionVideo, Long> {
    Optional<IntroductionVideo> findByInterviewerId(Long interviewer_id);
    Optional<IntroductionVideo> findByIdAndInterviewerId(Long id, Long interviwer_id);
}
