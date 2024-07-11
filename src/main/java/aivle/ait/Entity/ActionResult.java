package aivle.ait.Entity;

import aivle.ait.Dto.ActionResultDTO;
import aivle.ait.Dto.VoiceResultDTO;
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
    private double emotion_score;

    @Column(nullable = false)
    private double face_gesture_score;

    @Column(nullable = false)
    private double eyetrack_gesture_score;

    @Column(nullable = false)
    private double body_gesture_score;

    @Column(nullable = false)
    private double hand_count_score;

    @Column(nullable = false)
    private double action_score;

    // Result:ActionResult = 1:N
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id")
    private Result result;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "file_id")
    private File file;

    public void setDtoToObject(ActionResultDTO actionResultDTO){
        this.emotion_score = actionResultDTO.getEmotion_score();
        this.face_gesture_score = actionResultDTO.getFace_gesture_score();
        this.eyetrack_gesture_score = actionResultDTO.getEyetrack_gesture_score();
        this.body_gesture_score = actionResultDTO.getBody_gesture_score();
        this.hand_count_score = actionResultDTO.getHand_count_score();
        this.action_score = actionResultDTO.getAction_score();
    }

    // =====연관관계 메서드=====
    public void setResult(Result result) {
        this.result = result;
        result.getActionResults().add(this);
    }
    public void setFile(File file) {
        this.file = file;
        file.setActionResult(this);
    }
    public void deleteResult(){
        this.result = null;
    }
}
