package aivle.ait.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "result")
@Entity
@Getter
@Setter
public class Result extends Time {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String total_report;

    @Column(nullable = false)
    private int total_score;

    @Column(nullable = false)
    private int context_score;

    @Column(nullable = false)
    private int voice_score;

    @Column(nullable = false)
    private int action_score;

    // 외래키 소유
    @OneToOne
    @JoinColumn(name = "context_result_id")
    private ContextResult context_result;

    @OneToOne
    @JoinColumn(name = "voice_result_id")
    private VoiceResult voice_result;

    @OneToOne
    @JoinColumn(name = "action_result_id")
    private ActionResult action_result;

    // 읽기 전용
    @OneToOne(mappedBy = "result")
    private Interviewer interviewer;
}
