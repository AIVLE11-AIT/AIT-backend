package aivle.ait.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "pre_interview")
@Entity
@Getter
@Setter
public class PreInterview extends Time {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false)
    private String answer;

    // Interview_group:Pre_interview = 1:N
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_group_id")
    private InterviewGroup interviewgroup;
}
