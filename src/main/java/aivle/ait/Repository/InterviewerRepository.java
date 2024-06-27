package aivle.ait.Repository;

import aivle.ait.Entity.Interviewer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InterviewerRepository extends JpaRepository<Interviewer, Long> {
    Page<Interviewer> findByInterviewgroupId(Long interviewGroupId, Pageable pageable);
    Optional<Interviewer> findInterviewerByIdAndInterviewgroupId(Long id, Long interviewgroupId);
}
