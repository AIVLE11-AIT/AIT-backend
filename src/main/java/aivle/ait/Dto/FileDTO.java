package aivle.ait.Dto;

import aivle.ait.Entity.File;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class FileDTO {
    private Long id;

    private String video_path;

    private Long companyQna_id;
    private String companyQna_question;
    private String companyQna_answer;

    private Long interviewer_id;
    private String interviewer_name;
    private String interviewer_email;

    public FileDTO(File file) {
        this.id = file.getId();
        this.video_path = file.getVideo_path();

        this.companyQna_id = file.getCompanyQna().getId();
        this.companyQna_question = file.getCompanyQna().getQuestion();
        this.companyQna_answer = file.getCompanyQna().getAnswer();

        this.interviewer_id = file.getInterviewer().getId();
        this.interviewer_name = file.getInterviewer().getName();
        this.interviewer_email = file.getInterviewer().getEmail();
    }

    /* List<Object> -> List<Dto> 변환처리 */
    public static List<FileDTO> convertToDto(List<File> objectList) {
        return objectList.stream()
                .map(FileDTO::new)
                .collect(Collectors.toList());
    }

    /* Page<Object> -> Page<Dto> 변환처리 */
    public static Page<FileDTO> toDtoPage(Page<File> objectPage) {
        Page<FileDTO> dtos = objectPage.map(FileDTO::new);
        return dtos;
    }
}
