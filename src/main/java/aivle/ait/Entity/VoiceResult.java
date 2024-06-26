package aivle.ait.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "voice_result")
@Entity
@Getter
@Setter
public class VoiceResult extends Time {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double voice_size;

    @Column(nullable = false)
    private double voice_frequency;

    @Column(nullable = false)
    private double voice_speed;

    @Column(nullable = false)
    private double voice_wow;

    // 읽기 전용
    @OneToOne(mappedBy = "voice_result")
    private Result result;
}
