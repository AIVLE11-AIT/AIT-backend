package aivle.ait.Dto;

import aivle.ait.Entity.ActionResult;
import aivle.ait.Entity.ContextResult;
import aivle.ait.Entity.File;
import aivle.ait.Entity.Result;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class ContextResultDTO {
    private Long id;
    private double similarity_score;
    private double munmek_score;
    private double context_score;

    private Long result_id;
    private Long file_id;

    public ContextResultDTO(ContextResult contextResult) {
        this.id = contextResult.getId();
        this.similarity_score = contextResult.getSimilarity_score();
        this.munmek_score = contextResult.getMunmek_score();
        this.context_score = contextResult.getContext_score();

        this.result_id = contextResult.getResult().getId();
        this.file_id = contextResult.getFile().getId();
    }

    /* List<Object> -> List<Dto> 변환처리 */
    public static List<ContextResultDTO> convertToDto(List<ContextResult> objectList) {
        return objectList.stream()
                .map(ContextResultDTO::new)
                .collect(Collectors.toList());
    }

    /* Page<Object> -> Page<Dto> 변환처리 */
    public static Page<ContextResultDTO> toDtoPage(Page<ContextResult> objectPage) {
        Page<ContextResultDTO> dtos = objectPage.map(ContextResultDTO::new);
        return dtos;
    }
}
