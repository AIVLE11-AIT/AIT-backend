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
    private double lsa_score;

    @Column(nullable = false)
    private double emotion_score;

    @Column(nullable = false)
    private double munmek_score;

    @Column(nullable = false)
    private double context_score;

    // Result:ContextResult = 1:N
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id")
    private Result result;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "file_id")
    private File file;

    @OneToOne(mappedBy = "contextResult", cascade = CascadeType.ALL)
    private MunmekDetail munmekDetail;

    // =====연관관계 메서드=====
    public void setResult(Result result) {
        this.result = result;
        result.getContextResults().add(this);
    }
    public void setFile(File file) {
        this.file = file;
        file.setContextResult(this);
    }
}
