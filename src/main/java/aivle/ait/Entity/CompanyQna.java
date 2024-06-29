package aivle.ait.Entity;

import aivle.ait.Dto.CompanyQnaDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Table(name = "company_qna")
@Entity
@Getter
@Setter
public class CompanyQna extends Time {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false)
    private String answer;

    // Interview_group:company_qna = 1:N
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_group_id")
    private InterviewGroup interviewgroup;

    @OneToMany(mappedBy = "companyQna", cascade = CascadeType.ALL)
    private List<File> files = new ArrayList<>();

    public void setDtoToObject(CompanyQnaDTO companyQnaDTO){
        this.setQuestion(companyQnaDTO.getQuestion());
        this.setAnswer(companyQnaDTO.getAnswer());
    }
}
