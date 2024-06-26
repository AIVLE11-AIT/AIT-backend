package aivle.ait.Dto;

import aivle.ait.Entity.PreInterview;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}
