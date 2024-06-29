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
    private double voice_level;

    @Column(nullable = false)
    private double voice_speed;

    @Column(nullable = false)
    private double voice_intj;

    @Column(nullable = false)
    private double voice_score;

    // Result:VoiceResult = 1:N
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id")
    private Result result;

    @OneToOne
    @JoinColumn(name = "file_id")
    private File file;
}
