package aivle.ait.Dto;

import aivle.ait.Entity.InterviewGroup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class InterviewGroupDTO {
    private Long id;
    private String name;
    private LocalDateTime start_date;
    private LocalDateTime end_date;
    private int context_per;
    private int voice_per;
    private int action_per;
    private String language;

    private List<CompanyQnaDTO> companyQnas;
    private List<InterviewerDTO> interviewers;
    private Long company_id;
    private String company;

    public InterviewGroupDTO(InterviewGroup interviewgroup) {
        this.id = interviewgroup.getId();
        this.name = interviewgroup.getName();
        this.start_date = interviewgroup.getStart_date();
        this.end_date = interviewgroup.getEnd_date();
        this.context_per = interviewgroup.getContext_per();
        this.voice_per = interviewgroup.getVoice_per();
        this.action_per = interviewgroup.getAction_per();
        this.language = interviewgroup.getLanguage();

        this.companyQnas = interviewgroup.getCompanyQnas().stream().map(CompanyQnaDTO::new).collect(Collectors.toList());
        this.interviewers = interviewgroup.getInterviewers().stream().map(InterviewerDTO::new).collect(Collectors.toList());

        this.company_id = interviewgroup.getCompany().getId();
        this.company = interviewgroup.getCompany().getName();
    }

    /* List<Object> -> List<Dto> 변환처리 */
    public static List<InterviewGroupDTO> convertToDto(List<InterviewGroup> objectList) {
        return objectList.stream()
                .map(InterviewGroupDTO::new)
                .collect(Collectors.toList());
    }

    /* Page<Object> -> Page<Dto> 변환처리 */
    public static Page<InterviewGroupDTO> toDtoPage(Page<InterviewGroup> objectPage) {
        Page<InterviewGroupDTO> dtos = objectPage.map(InterviewGroupDTO::new);
        return dtos;
    }
}
