package aivle.ait.Dto;

import aivle.ait.Entity.InterviewGroup;
import aivle.ait.Entity.PreInterview;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class PreInterviewDTO {
    private Long id;
    private String question;
    private String answer;
    private Long interview_group_id;
    private String interview_group;

    public PreInterviewDTO(PreInterview pre_interview) {
        this.id = pre_interview.getId();
        this.question = pre_interview.getQuestion();
        this.answer = pre_interview.getAnswer();

        this.interview_group_id = pre_interview.getInterviewgroup().getId();
        this.interview_group = pre_interview.getInterviewgroup().getName();
    }

    /* List<Object> -> List<Dto> 변환처리 */
    public static List<PreInterviewDTO> convertToDto(List<PreInterview> objectList) {
        return objectList.stream()
                .map(PreInterviewDTO::new)
                .collect(Collectors.toList());
    }

    /* Page<Object> -> Page<Dto> 변환처리 */
    public static Page<PreInterviewDTO> toDtoPage(Page<PreInterview> objectPage) {
        Page<PreInterviewDTO> dtos = objectPage.map(PreInterviewDTO::new);
        return dtos;
    }
}
