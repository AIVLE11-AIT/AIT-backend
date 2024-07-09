package aivle.ait.Dto;

import aivle.ait.Entity.MunmekDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class MunmekDetailDTO {
    private Long id;
    private String relevance;
    private String logicality;
    private String clarity;
    private String questionComprehension;

    private Long contextResultId;

    public MunmekDetailDTO(MunmekDetail munmekDetail) {
        this.id = munmekDetail.getId();
        this.relevance = munmekDetail.getRelevance();
        this.logicality = munmekDetail.getLogicality();
        this.clarity = munmekDetail.getClarity();
        this.questionComprehension = munmekDetail.getQuestionComprehension();

        this.contextResultId = munmekDetail.getContextResult().getId();
    }

    /* List<Object> -> List<Dto> 변환처리 */
    public static List<MunmekDetailDTO> convertToDto(List<MunmekDetail> objectList) {
        return objectList.stream()
                .map(MunmekDetailDTO::new)
                .collect(Collectors.toList());
    }

    /* Page<Object> -> Page<Dto> 변환처리 */
    public static Page<MunmekDetailDTO> toDtoPage(Page<MunmekDetail> objectPage) {
        Page<MunmekDetailDTO> dtos = objectPage.map(MunmekDetailDTO::new);
        return dtos;
    }
}
