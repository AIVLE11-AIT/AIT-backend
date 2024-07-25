package aivle.ait.Entity;

import aivle.ait.Dto.InterviewerQnaDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "interviewer_qna")
@Entity
@Getter
@Setter
public class InterviewerQna extends Time {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String question;

    // 읽기 전용
    @OneToOne(mappedBy = "interviewerQna", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private File file;

    // Interviewer:InterviewerQna = 1:N
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "interviewer_id")
    private Interviewer interviewer;

    public void setDtoToObject(InterviewerQnaDTO interviewerQnaDTO){
        this.question = interviewerQnaDTO.getQuestion();
    }

    // =====연관관계 메서드=====
    public void setInterviewer(Interviewer interviewer) {
        this.interviewer = interviewer;
        interviewer.getInterviewerQnas().add(this);
    }

}
