package aivle.ait.Service;

import aivle.ait.Dto.MunmekDetailDTO;
import aivle.ait.Entity.MunmekDetail;
import aivle.ait.Repository.MunmekDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MunmekDetailService {
    private final MunmekDetailRepository munmekDetailRepository;

    public MunmekDetailDTO create(MunmekDetailDTO munmekDetailDTO) {
        MunmekDetail munmekDetail = new MunmekDetail();
        munmekDetail.setDtoToObject(munmekDetailDTO);

        MunmekDetail createdMunmekDetail = munmekDetailRepository.save(munmekDetail);

        return new MunmekDetailDTO(createdMunmekDetail);
    }
}
