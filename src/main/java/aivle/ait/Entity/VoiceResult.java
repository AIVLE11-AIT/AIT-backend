package aivle.ait.Entity;

import aivle.ait.Dto.VoiceResultDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "voice_result")
@Entity
@Getter
@Setter
public class VoiceResult extends Time {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double voice_level;

    @Column(nullable = false)
    private double voice_speed;

    @Column(nullable = false)
    private double voice_intj;

    @Column(nullable = false)
    private double voice_score;

    // Result:VoiceResult = 1:N
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id")
    private Result result;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "file_id")
    private File file;

    public void setDtoToObject(VoiceResultDTO voiceResultDTO){
        this.voice_level = voiceResultDTO.getVoice_level();
        this.voice_speed = voiceResultDTO.getVoice_speed();
        this.voice_intj = voiceResultDTO.getVoice_intj();
        this.voice_score = voiceResultDTO.getVoice_score();
    }

    // =====연관관계 메서드=====
    public void setResult(Result result) {
        this.result = result;
        result.getVoiceResults().add(this);
    }
    public void setFile(File file) {
        this.file = file;
        file.setVoiceResult(this);
    }
}
