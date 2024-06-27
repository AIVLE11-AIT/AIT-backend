package aivle.ait.Entity;

import aivle.ait.Dto.InterviewerDTO;
import aivle.ait.Dto.PreInterviewDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Table(name = "interviewer")
@Entity
@Getter
@Setter
public class Interviewer extends Time {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 50, nullable = false)
    private String email;

    @Column(nullable = false)
    private LocalDateTime birth;

    @Column(nullable = false)
    private String image_path;

    @Column(nullable = false)
    private String video_path;

    @Column(nullable = false)
    private String voice_path;

    @Column(nullable = false)
    private String cover_letter;

    private String voice_default;
    private String distance_default;

    @OneToOne
    @JoinColumn(name = "result_id")
    private Result result;

    // Interview_group:Interviewer = 1:N
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_group_id")
    private InterviewGroup interviewgroup;

    public void setDtoToObject(InterviewerDTO interviewerDTO){
        this.setName(interviewerDTO.getName());
        this.setEmail(interviewerDTO.getEmail());
        this.setBirth(interviewerDTO.getBirth());
        this.setImage_path(interviewerDTO.getImage_path());
        this.setVideo_path(interviewerDTO.getVideo_path());
        this.setCover_letter(interviewerDTO.getCover_letter());
    }
}
