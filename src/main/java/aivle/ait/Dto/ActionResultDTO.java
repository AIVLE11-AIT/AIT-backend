package aivle.ait.Dto;

import aivle.ait.Entity.ActionResult;
import aivle.ait.Entity.VoiceResult;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class ActionResultDTO {
    private Long id;
    private double emotion_score;
    private double face_gesture_score;
    private double eyetrack_gesture_score;
    private double body_gesture_score;
    private double hand_count_score;
    private double action_score;

    private Long result_id;
    private Long file_id;

    public ActionResultDTO(ActionResult actionResult) {
        this.id = actionResult.getId();
        this.emotion_score = actionResult.getEmotion_score();
        this.face_gesture_score = actionResult.getFace_gesture_score();
        this.eyetrack_gesture_score = actionResult.getEyetrack_gesture_score();
        this.body_gesture_score = actionResult.getBody_gesture_score();
        this.hand_count_score = actionResult.getHand_count_score();
        this.action_score = actionResult.getAction_score();

        this.result_id = actionResult.getResult().getId();
        this.file_id = actionResult.getFile().getId();
    }

    /* List<Object> -> List<Dto> 변환처리 */
    public static List<ActionResultDTO> convertToDto(List<ActionResult> objectList) {
        return objectList.stream()
                .map(ActionResultDTO::new)
                .collect(Collectors.toList());
    }

    /* Page<Object> -> Page<Dto> 변환처리 */
    public static Page<ActionResultDTO> toDtoPage(Page<ActionResult> objectPage) {
        Page<ActionResultDTO> dtos = objectPage.map(ActionResultDTO::new);
        return dtos;
    }
}
