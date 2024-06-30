package aivle.ait.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "action_result")
@Entity
@Getter
@Setter
public class ActionResult extends Time {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double angry;

    @Column(nullable = false)
    private double disgust;

    @Column(nullable = false)
    private double fear;

    @Column(nullable = false)
    private double happy;

    @Column(nullable = false)
    private double sad;

    @Column(nullable = false)
    private double surprise;

    @Column(nullable = false)
    private double neutral;

    @Column(nullable = false)
    private double face_gesture_score;

    @Column(nullable = false)
    private double eyetrack_gesture_score;

    @Column(nullable = false)
    private double body_gesture_score;

    @Column(nullable = false)
    private double hand_count_score;

    // Result:ActionResult = 1:N
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id")
    private Result result;

    @OneToOne
    @JoinColumn(name = "file_id")
    private File file;

    // =====연관관계 메서드=====
    public void setResult(Result result) {
        this.result = result;
        result.getActionResults().add(this);
    }
    public void setFile(File file) {
        this.file = file;
        file.setActionResult(this);
    }
}
