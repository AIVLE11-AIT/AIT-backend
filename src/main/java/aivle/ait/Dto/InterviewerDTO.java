package aivle.ait.Dto;

import aivle.ait.Entity.Interviewer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class InterviewerDTO {
    private Long id;
    private String name;
    private String email;
    private LocalDateTime birth;
    private String image_path;
    private String video_path;
    private String voice_path;
    private String cover_letter;
    private String voice_default;
    private String distance_default;


    private Long result_id;
    private String result;
    private Long interview_group_id;
    private String interview_group;

    public InterviewerDTO(Interviewer interviewer) {
        this.id = interviewer.getId();
        this.name = interviewer.getName();
        this.email = interviewer.getEmail();
        this.birth = interviewer.getBirth();
        this.image_path = interviewer.getImage_path();
        this.video_path = interviewer.getVideo_path();
        this.voice_path = interviewer.getVoice_path();
        this.cover_letter = interviewer.getCover_letter();
        this.voice_default = interviewer.getVoice_default();
        this.distance_default = interviewer.getDistance_default();

        this.result_id = interviewer.getResult().getId();
        this.result = interviewer.getResult().getTotal_report();
        this.interview_group_id = interviewer.getInterviewgroup().getId();
        this.interview_group = interviewer.getInterviewgroup().getName();
    }
}
