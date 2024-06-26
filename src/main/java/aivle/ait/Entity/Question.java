package aivle.ait.Entity;

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

    // 외래키 소유
    @OneToOne
    @JoinColumn(name = "answer_id")
    private Answer answer;

    // Company:Question = 1:N
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;
}
