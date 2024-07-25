package aivle.ait.Repository;

import aivle.ait.Entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Optional<Answer> findAnswerByQuestionId(Long questionId);

    @Modifying
    @Query("UPDATE Answer c SET c.question = null WHERE c.id = :answerId")
    void detachAnswerFromQuestion(@Param("answerId") Long answerId);
}
