package aivle.ait.Repository;

import aivle.ait.Entity.PreInterview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PreInterviewRepository extends JpaRepository<PreInterview, Long> {
    Page<PreInterview> findByInterviewgroupId(Long interviewGroupId, Pageable pageable);
    Optional<PreInterview> findPreInterviewByIdAndInterviewgroupId(Long id, Long interviewgroupId);
}
