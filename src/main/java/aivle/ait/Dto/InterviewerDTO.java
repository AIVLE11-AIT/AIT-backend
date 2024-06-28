package aivle.ait.Dto;

import aivle.ait.Entity.Interviewer;
import aivle.ait.Entity.PreInterview;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
        this.cover_letter = interviewer.getCover_letter();
        this.voice_default = interviewer.getVoice_default();
        this.distance_default = interviewer.getDistance_default();

        if (interviewer.getResult() != null){
            this.result_id = interviewer.getResult().getId();
            this.result = interviewer.getResult().getTotal_report();
        }
        this.interview_group_id = interviewer.getInterviewgroup().getId();
        this.interview_group = interviewer.getInterviewgroup().getName();
    }

    /* List<Object> -> List<Dto> 변환처리 */
    public static List<InterviewerDTO> convertToDto(List<Interviewer> objectList) {
        return objectList.stream()
                .map(InterviewerDTO::new)
                .collect(Collectors.toList());
    }

    /* Page<Object> -> Page<Dto> 변환처리 */
    public static Page<InterviewerDTO> toDtoPage(Page<Interviewer> objectPage) {
        Page<InterviewerDTO> dtos = objectPage.map(InterviewerDTO::new);
        return dtos;
    }
}
