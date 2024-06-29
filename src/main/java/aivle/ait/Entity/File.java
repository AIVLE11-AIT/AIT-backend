package aivle.ait.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "file")
@Entity
@Getter
@Setter
public class File extends Time {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String video_path;

    @OneToOne(mappedBy = "file")
    private ContextResult contextResult;

    @OneToOne(mappedBy = "file")
    private VoiceResult voiceResult;

    @OneToOne(mappedBy = "file")
    private ActionResult actionResult;

    // CompanyQna:File = 1:N
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_qna_id")
    private CompanyQna companyQna;

    // Interviewer:File = 1:N
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interviewer_id")
    private Interviewer interviewer;
}
