package aivle.ait.Repository;

import aivle.ait.Entity.Company;
import aivle.ait.Entity.InterviewGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewGroupRepository extends JpaRepository<InterviewGroup, Long> {
    Page<InterviewGroup> findByCompany(Company company, Pageable pageable);
}
