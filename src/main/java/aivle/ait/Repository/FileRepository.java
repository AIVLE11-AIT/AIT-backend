package aivle.ait.Repository;

import aivle.ait.Entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {
    Optional<List<File>> findByInterviewerId(Long interviewerId);
    Optional<File> findByInterviewerIdAndCompanyQnaId(Long interviewerId, Long companyId);
    Optional<File> findByInterviewerQnaId(Long interviewerQnaId);
}
