package aivle.ait.Dto;

import aivle.ait.Entity.InterviewerQna;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class InterviewerQnaDTO {
    private Long id;
    private String question;
    private String answer;

    private Long interviewerId;

    public InterviewerQnaDTO(InterviewerQna interviewerQna) {
        this.id = interviewerQna.getId();
        this.question = interviewerQna.getQuestion();

        this.interviewerId = interviewerQna.getInterviewer().getId();
    }

    /* List<Object> -> List<Dto> 변환처리 */
    public static List<InterviewerQnaDTO> convertToDto(List<InterviewerQna> objectList) {
        return objectList.stream()
                .map(InterviewerQnaDTO::new)
                .collect(Collectors.toList());
    }

    /* Page<Object> -> Page<Dto> 변환처리 */
    public static Page<InterviewerQnaDTO> toDtoPage(Page<InterviewerQna> objectPage) {
        Page<InterviewerQnaDTO> dtos = objectPage.map(InterviewerQnaDTO::new);
        return dtos;
    }
}
