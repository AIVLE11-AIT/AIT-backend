package aivle.ait.Dto;

import aivle.ait.Entity.Question;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
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
        this.company_id = question.getCompany().getId();
        this.company = question.getCompany().getName();

        if (question.getAnswer() != null) {
            this.answer_id = question.getAnswer().getId();
            this.answer = question.getAnswer().getContent();
        }
    }

    /* List<Object> -> List<Dto> 변환처리 */
    public static List<QuestionDTO> convertToDto(List<Question> objectList) {
        return objectList.stream()
                .map(QuestionDTO::new)
                .collect(Collectors.toList());
    }

    /* Page<Object> -> Page<Dto> 변환처리 */
    public static Page<QuestionDTO> toDtoPage(Page<Question> objectPage) {
        Page<QuestionDTO> dtos = objectPage.map(QuestionDTO::new);
        return dtos;
    }
}
