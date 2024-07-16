package aivle.ait.Dto;

import aivle.ait.Entity.CompanyQna;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class CompanyQnaDTO {
    private Long id;
    private String question;
    private String answer;
    private Long interview_group_id;
    private String interview_group;
    private List<FileDTO> files;

    public CompanyQnaDTO(CompanyQna companyQna) {
        this.id = companyQna.getId();
        this.question = companyQna.getQuestion();

        if (companyQna.getAnswer() != null)
            this.answer = companyQna.getAnswer();

        this.interview_group_id = companyQna.getInterviewgroup().getId();
        this.interview_group = companyQna.getInterviewgroup().getName();

        this.files = companyQna.getFiles().stream().map(FileDTO::new).collect(Collectors.toList());
    }

    /* List<Object> -> List<Dto> 변환처리 */
    public static List<CompanyQnaDTO> convertToDto(List<CompanyQna> objectList) {
        return objectList.stream()
                .map(CompanyQnaDTO::new)
                .collect(Collectors.toList());
    }

    /* Page<Object> -> Page<Dto> 변환처리 */
    public static Page<CompanyQnaDTO> toDtoPage(Page<CompanyQna> objectPage) {
        Page<CompanyQnaDTO> dtos = objectPage.map(CompanyQnaDTO::new);
        return dtos;
    }
}
