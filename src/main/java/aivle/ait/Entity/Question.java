package aivle.ait.Entity;

import aivle.ait.Dto.QuestionDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "question")
@Entity
@Getter
@Setter
public class Question extends Time {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    // 읽기 전용
    @OneToOne(mappedBy = "question", fetch = FetchType.LAZY)
    private Answer answer;

    // Company:Question = 1:N
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    public void setDtoToObject(QuestionDTO questionDTO){
        this.title = questionDTO.getTitle();
        this.content = questionDTO.getContent();
    }

    // =====연관관계 메서드=====
    public void setCompany(Company company) {
        this.company = company;
        company.getQuestions().add(this);
    }
}
