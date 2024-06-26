package aivle.ait.Dto;

import aivle.ait.Entity.Answer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerDTO {
    private Long id;
    private String content;

    private Long question_id;
    private String question;

    public AnswerDTO(Answer answer) {
        this.id = answer.getId();
        this.content = answer.getContent();

        this.question_id = answer.getQuestion().getId();
        this.question = answer.getQuestion().getContent();
    }
}
