package aivle.ait.Repository;

import aivle.ait.Entity.Company;
import aivle.ait.Entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
//    @Cacheable("boardsByTitleContaining")
//    List<Board> findByTitleContaining(String keyword);

    List<Question> findCustomByTitleContaining(String title);
    Page<Question> findByCompanyId(Long companyId, Pageable pageable);
    Page<Question> findAll(Pageable pageable);
}
