package aivle.ait.Entity;

import aivle.ait.Dto.AnswerDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "answer")
@Entity
@Getter
@Setter
public class Answer extends Time{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    // 외래키 소유
    @OneToOne
    @JoinColumn(name = "question_id")
    private Question question;

    public void setDtoToObject(AnswerDTO answerDTO){
        this.content = answerDTO.getContent();
    }

    // =====연관관계 메서드=====
    public void setQuestion(Question question) {
        this.question = question;
        question.setAnswer(this);
    }
}
