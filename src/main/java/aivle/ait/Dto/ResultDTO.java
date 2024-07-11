package aivle.ait.Dto;

import aivle.ait.Entity.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class ResultDTO {
    private Long id;
    private String total_report;
    private int total_similarity_score;
    private int total_lsa_score;
    private int total_emotion_score;
    private int total_munmek_score;
    private int total_voice_level;
    private int total_voice_speed;
    private int total_voice_intj;
    private int total_face_gesture_score;
    private int total_eyetrack_gesture_score;
    private int total_body_gesture_score;
    private int total_hand_count_score;
    private int total_face_emotion_score;
    private int total_score;
    private int context_score;
    private int voice_score;
    private int action_score;

    private List<ContextResultDTO> contextResults;
    private List<VoiceResultDTO> voiceResult;
    private List<ActionResultDTO> actionResults;

    private Long interviewerId;

    public ResultDTO(Result result) {
        this.id = result.getId();
        this.total_report = result.getTotal_report();
        this.total_similarity_score = result.getTotal_similarity_score();
        this.total_lsa_score = result.getTotal_lsa_score();
        this.total_emotion_score = result.getTotal_emotion_score();
        this.total_munmek_score = result.getTotal_munmek_score();
        this.total_voice_level = result.getTotal_voice_level();
        this.total_voice_speed = result.getTotal_voice_speed();
        this.total_voice_intj = result.getTotal_voice_intj();
        this.total_face_gesture_score = result.getTotal_face_gesture_score();
        this.total_eyetrack_gesture_score = result.getTotal_eyetrack_gesture_score();
        this.total_body_gesture_score = result.getTotal_body_gesture_score();
        this.total_hand_count_score = result.getTotal_hand_count_score();
        this.total_face_emotion_score = result.getTotal_face_emotion_score();
        this.total_score = result.getTotal_score();
        this.context_score = result.getContext_score();
        this.voice_score = result.getVoice_score();
        this.action_score = result.getAction_score();

        this.contextResults = result.getContextResults().stream().map(ContextResultDTO::new).collect(Collectors.toList());
        this.voiceResult = result.getVoiceResults().stream().map(VoiceResultDTO::new).collect(Collectors.toList());
        this.actionResults = result.getActionResults().stream().map(ActionResultDTO::new).collect(Collectors.toList());

        this.interviewerId = result.getInterviewer().getId();
    }

    /* List<Object> -> List<Dto> 변환처리 */
    public static List<ResultDTO> convertToDto(List<Result> objectList) {
        return objectList.stream()
                .map(ResultDTO::new)
                .collect(Collectors.toList());
    }

    /* Page<Object> -> Page<Dto> 변환처리 */
    public static Page<ResultDTO> toDtoPage(Page<Result> objectPage) {
        Page<ResultDTO> dtos = objectPage.map(ResultDTO::new);
        return dtos;
    }
}
