package aivle.ait.Entity;

import aivle.ait.Dto.InterviewerDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(nullable = true)
    private String image_path;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String cover_letter;
    // 읽기 전용
    @OneToOne(mappedBy = "interviewer", fetch = FetchType.LAZY)
    private Result result;

    // Interview_group:Interviewer = 1:N
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_group_id")
    private InterviewGroup interviewgroup;

    @OneToMany(mappedBy = "interviewer", cascade = CascadeType.ALL)
    private List<File> files = new ArrayList<>();

    @OneToMany(mappedBy = "interviewer", cascade = CascadeType.ALL)
    private List<InterviewerQna> interviewerQnas = new ArrayList<>();

    public void setDtoToObject(InterviewerDTO interviewerDTO){
        this.setName(interviewerDTO.getName());
        this.setEmail(interviewerDTO.getEmail());
        this.setBirth(interviewerDTO.getBirth());
        this.setImage_path(interviewerDTO.getImage_path());
        this.setCover_letter(interviewerDTO.getCover_letter());
    }

    // =====연관관계 메서드=====
    public void setInterviewgroup(InterviewGroup interviewgroup) {
        this.interviewgroup = interviewgroup;
        interviewgroup.getInterviewers().add(this);
    }
}
