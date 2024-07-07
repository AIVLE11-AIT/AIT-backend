package aivle.ait.Dto;

import aivle.ait.Entity.File;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class FileDTO {
    private Long id;
    private String video_path;
    private Boolean isGroup;
    private Long companyQna_id;
    private Long interviewer_id;
    private Long context_result_id;
    private Long voice_result_id;
    private Long action_result_id;
    private Long interviewerQna_id;

    public FileDTO(File file) {
        this.id = file.getId();
        this.video_path = file.getVideo_path();
        this.isGroup = file.getIsGroup();
        this.companyQna_id = file.getCompanyQna().getId();
        this.interviewer_id = file.getInterviewer().getId();
        this.interviewerQna_id = file.getInterviewerQna().getId();

        if (file.getContextResult() != null)
            this.context_result_id = file.getContextResult().getId();
        if (file.getVoiceResult() != null)
            this.voice_result_id = file.getVoiceResult().getId();
        if (file.getActionResult() != null)
            this.action_result_id = file.getActionResult().getId();
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
