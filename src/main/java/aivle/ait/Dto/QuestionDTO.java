package aivle.ait.Dto;

import aivle.ait.Entity.Question;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class QuestionDTO {
    private Long id;
    private String title;
    private String content;
    private Long answer_id;
    private String answer;
    private Long company_id;
    private String company;

    public QuestionDTO(Question question) {
        this.id = question.getId();
        this.title = question.getTitle();
        this.content = question.getContent();
        this.answer_id = question.getAnswer().getId();
        this.answer = question.getAnswer().getContent();
        this.company_id = question.getCompany().getId();
        this.company = question.getCompany().getName();
    }

    /* Object -> Dto 변환처리 */
    public static List<QuestionDTO> convertToDto(List<Question> questions) {
        return questions.stream()
                .map(QuestionDTO::new)
                .collect(Collectors.toList());
    }

    /* Page<Object> -> Page<Dto> 변환처리 */
    public static Page<QuestionDTO> toDtoPage(Page<Question> objectPage) {
        Page<QuestionDTO> questionDTOS = objectPage.map(QuestionDTO::new);
        return questionDTOS;
    }
}
