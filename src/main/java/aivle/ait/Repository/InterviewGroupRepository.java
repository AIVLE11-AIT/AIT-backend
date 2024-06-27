package aivle.ait.Repository;

import aivle.ait.Entity.InterviewGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InterviewGroupRepository extends JpaRepository<InterviewGroup, Long> {
    Page<InterviewGroup> findByCompanyId(Long companyId, Pageable pageable);
    Optional<InterviewGroup> findInterviewGroupByIdAndCompanyId(Long id, Long companyId);
}
