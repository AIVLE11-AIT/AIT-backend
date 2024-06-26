package aivle.ait.Entity;

import aivle.ait.Dto.InterviewGroupDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "interview_group")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class InterviewGroup extends Time {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime start_date;

    @Column(nullable = false)
    private LocalDateTime end_date;

    @Column(nullable = false)
    private int context_per;

    @Column(nullable = false)
    private int voice_per;

    @Column(nullable = false)
    private int action_per;

    @OneToMany(mappedBy = "interviewgroup", cascade = CascadeType.ALL)
    private List<PreInterview> pre_interviews = new ArrayList<>();

    @OneToMany(mappedBy = "interviewgroup", cascade = CascadeType.ALL)
    private List<Interviewer> interviewers = new ArrayList<>();

    // Company:Interview_group = 1:N
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    public void setDtoToObject(InterviewGroupDTO interviewGroupDTO){
        this.setName(interviewGroupDTO.getName());
        this.setStart_date(interviewGroupDTO.getStart_date());
        this.setEnd_date(interviewGroupDTO.getEnd_date());
        this.setContext_per(interviewGroupDTO.getContext_per());
        this.setVoice_per(interviewGroupDTO.getVoice_per());
        this.setAction_per(interviewGroupDTO.getAction_per());
    }
}
