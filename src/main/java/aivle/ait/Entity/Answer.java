package aivle.ait.Entity;

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

    // 읽기 전용
    @OneToOne(mappedBy = "answer")
    private Question question;
}
