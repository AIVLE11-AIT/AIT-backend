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
    List<Question> findByCompany(Company company);
    Page<Question> findAll(Pageable pageable);
}
