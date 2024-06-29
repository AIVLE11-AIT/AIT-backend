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
    private double munmek_score;

    @Column(nullable = false)
    private double context_score;

    // Result:ContextResult = 1:N
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id")
    private Result result;

    @OneToOne
    @JoinColumn(name = "file_id")
    private File file;
}
