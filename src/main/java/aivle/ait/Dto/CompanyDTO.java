package aivle.ait.Dto;

import aivle.ait.Entity.Company;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class CompanyDTO {
    private Long id;
    private String name;
    private String password;
    private String email;
    private String role; // USER, ADMIN

    private List<InterviewGroupDTO> interview_groups;
    private List<QuestionDTO> questions;

    private String roles;

    public CompanyDTO(Company company) {
        this.id = company.getId();
        this.name = company.getName();
        this.email = company.getEmail();
        this.role = company.getRole();

        this.interview_groups = company.getInterviewGroups().stream().map(InterviewGroupDTO::new).collect(Collectors.toList());
        this.questions = company.getQuestions().stream().map(QuestionDTO::new).collect(Collectors.toList());
    }
}
