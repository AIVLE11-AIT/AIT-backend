package aivle.ait.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "result", cascade = CascadeType.ALL)
    private List<ContextResult> contextResults = new ArrayList<>();

    @OneToMany(mappedBy = "result", cascade = CascadeType.ALL)
    private List<VoiceResult> voiceResults = new ArrayList<>();

    @OneToMany(mappedBy = "result", cascade = CascadeType.ALL)
    private List<ActionResult> actionResults = new ArrayList<>();

    // 읽기 전용
    @OneToOne(mappedBy = "result")
    private Interviewer interviewer;
}
