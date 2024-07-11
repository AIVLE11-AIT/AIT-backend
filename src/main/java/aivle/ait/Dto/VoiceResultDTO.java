package aivle.ait.Dto;

import aivle.ait.Entity.File;
import aivle.ait.Entity.Question;
import aivle.ait.Entity.Result;
import aivle.ait.Entity.VoiceResult;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class VoiceResultDTO {
    private Long id;
    private double voice_level;
    private double voice_speed;
    private double voice_intj;
    private double voice_score;

    private Long result_id;
    private Long file_id;

    public VoiceResultDTO(VoiceResult voiceResult) {
        this.id = voiceResult.getId();
        this.voice_level = voiceResult.getVoice_level();
        this.voice_speed = voiceResult.getVoice_speed();
        this.voice_intj = voiceResult.getVoice_intj();
        this.voice_score = voiceResult.getVoice_score();

        if (voiceResult.getResult() != null)
            this.result_id = voiceResult.getResult().getId();
        this.file_id = voiceResult.getFile().getId();
    }

    /* List<Object> -> List<Dto> 변환처리 */
    public static List<VoiceResultDTO> convertToDto(List<VoiceResult> objectList) {
        return objectList.stream()
                .map(VoiceResultDTO::new)
                .collect(Collectors.toList());
    }

    /* Page<Object> -> Page<Dto> 변환처리 */
    public static Page<VoiceResultDTO> toDtoPage(Page<VoiceResult> objectPage) {
        Page<VoiceResultDTO> dtos = objectPage.map(VoiceResultDTO::new);
        return dtos;
    }
}
