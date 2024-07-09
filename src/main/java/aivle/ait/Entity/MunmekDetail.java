package aivle.ait.Entity;

import aivle.ait.Dto.MunmekDetailDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "munmekDetail")
@Entity
@Getter
@Setter
public class MunmekDetail extends Time {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String relevance;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String logicality;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String clarity;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String questionComprehension;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "context_result_id")
    private ContextResult contextResult;

    public void setDtoToObject(MunmekDetailDTO munmekDetailDTO){
        this.relevance = munmekDetailDTO.getRelevance();
        this.logicality = munmekDetailDTO.getLogicality();
        this.clarity = munmekDetailDTO.getClarity();
        this.questionComprehension = munmekDetailDTO.getQuestionComprehension();
    }

    // =====연관관계 메서드=====
    public void setContextResult(ContextResult contextResult) {
        this.contextResult = contextResult;
        contextResult.setMunmekDetail(this);
    }
}
