package aivle.ait.Repository;

import aivle.ait.Entity.InterviewerQna;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InterviewerQnaRepository extends JpaRepository<InterviewerQna, Long>  {
    Optional<InterviewerQna> findInterviewerByIdAndInterviewerId(Long id, Long interviewerId);
}
