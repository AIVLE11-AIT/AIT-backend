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
    private int total_similarity_score;

    @Column(nullable = false)
    private int total_lsa_score;

    @Column(nullable = false)
    private int total_emotion_score;

    @Column(nullable = false)
    private int total_munmek_score;

    @Column(nullable = false)
    private int total_voice_level;

    @Column(nullable = false)
    private int total_voice_speed;

    @Column(nullable = false)
    private int total_voice_intj;

    @Column(nullable = false)
    private int total_face_gesture_score;

    @Column(nullable = false)
    private int total_eyetrack_gesture_score;

    @Column(nullable = false)
    private int total_body_gesture_score;

    @Column(nullable = false)
    private int total_hand_count_score;

    @Column(nullable = false)
    private int total_face_emotion_score;

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interviewer_id")
    private Interviewer interviewer;

    public void setZero(){
        this.total_similarity_score = 0;
        this.total_lsa_score = 0;
        this.total_emotion_score = 0;
        this.total_munmek_score = 0;
        this.total_voice_level = 0;
        this.total_voice_speed = 0;
        this.total_voice_intj = 0;
        this.total_face_gesture_score = 0;
        this.total_eyetrack_gesture_score = 0;
        this.total_body_gesture_score = 0;
        this.total_hand_count_score = 0;
        this.total_face_emotion_score = 0;
        this.total_score = 0;
        this.context_score = 0;
        this.voice_score = 0;
        this.action_score = 0;
    }

    // =====연관관계 메서드=====
    public void setInterviewer(Interviewer interviewer) {
        this.interviewer = interviewer;
        interviewer.setResult(this);
    }
}
