package aivle.ait.Dto;

import aivle.ait.Entity.IntroductionVideo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class IntroductionVideoDTO {
    private Long id;
    private String video_path;

    private Long interviewer_id;

    public IntroductionVideoDTO(IntroductionVideo introductionVideo) {
        this.id = introductionVideo.getId();
        this.video_path = introductionVideo.getVideo_path();
        this.interviewer_id = introductionVideo.getInterviewer().getId();
    }
}
