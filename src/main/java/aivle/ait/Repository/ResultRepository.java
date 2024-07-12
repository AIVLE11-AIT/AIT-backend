package aivle.ait.Repository;

import aivle.ait.Entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResultRepository extends JpaRepository<Result, Long> {
    Optional<Result> findByInterviewerId(Long interviewerId);
}
