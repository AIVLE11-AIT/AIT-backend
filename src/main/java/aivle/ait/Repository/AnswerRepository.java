package aivle.ait.Repository;

import aivle.ait.Entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Optional<Answer> findAnswerByQuestionId(Long questionId);
}
