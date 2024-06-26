package aivle.ait.Repository;

import aivle.ait.Entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    boolean existsByEmail(String email);
    Company findByEmail(String email);
}
