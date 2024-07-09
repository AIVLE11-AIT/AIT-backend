package aivle.ait.Entity;

import aivle.ait.Dto.IntroductionVideoDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name="introduction_video")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class IntroductionVideo extends Time {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String video_path;

    // 외래키
    // Interviewer:IntroductionVideo = 1:1
    @OneToOne
    @JoinColumn(name="interviewer_id")
    private Interviewer interviewer;

    public void setDtoToObject(IntroductionVideoDTO introductionVideoDTO) {
        this.video_path = introductionVideoDTO.getVideo_path();
    }

    // =====연관관계 메서드=====
    public void setInterviewer(Interviewer interviewer) {
        this.interviewer = interviewer;
        interviewer.setIntroductionVideo(this);
    }
}
