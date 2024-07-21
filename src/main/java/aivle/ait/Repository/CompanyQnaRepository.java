package aivle.ait.Repository;

import aivle.ait.Entity.CompanyQna;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyQnaRepository extends JpaRepository<CompanyQna, Long> {
    Page<CompanyQna> findByInterviewgroupId(Long interviewGroupId, Pageable pageable);
    Optional<CompanyQna> findCompanyQnaByIdAndInterviewgroupId(Long id, Long interviewgroupId);
    Optional<CompanyQna> findCompanyQnaByIdAndInterviewerId(Long id, Long interviewerId);
}
