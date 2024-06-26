package aivle.ait.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "context_result")
@Entity
@Getter
@Setter
public class ContextResult extends Time {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double similarity_score;

    @Column(nullable = false)
    private double context_score;

    // 읽기 전용
    @OneToOne(mappedBy = "context_result")
    private Result result;
}
