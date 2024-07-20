package aivle.ait.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VisualQnADTO {
    private String question;
    private String answer;

    public VisualQnADTO(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }
}
