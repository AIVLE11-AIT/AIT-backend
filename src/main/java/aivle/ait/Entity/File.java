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

    @Column(nullable = false)
    private Boolean isGroup;

    @Column(columnDefinition = "LONGTEXT")
    private String answer;

    @OneToOne(mappedBy = "file", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private ContextResult contextResult;

    @OneToOne(mappedBy = "file", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private VoiceResult voiceResult;

    @OneToOne(mappedBy = "file", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private ActionResult actionResult;

    // CompanyQna:File = 1:N
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_qna_id")
    private CompanyQna companyQna;

    // Interviewer:File = 1:N
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interviewer_id")
    private Interviewer interviewer;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "interviewer_qna_id")
    private InterviewerQna interviewerQna;

    // =====연관관계 메서드=====
    public void setCompanyQna(CompanyQna companyQna) {
        this.companyQna = companyQna;
        companyQna.getFiles().add(this);
    }
    public void setInterviewer(Interviewer interviewer) {
        this.interviewer = interviewer;
        interviewer.getFiles().add(this);
    }
    public void setInterviewerQna(InterviewerQna interviewerQna) {
        this.interviewerQna = interviewerQna;
        interviewerQna.setFile(this);
    }

}
