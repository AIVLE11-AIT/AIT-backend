package aivle.ait.Service;

import aivle.ait.Dto.ContextResultDTO;
import aivle.ait.Dto.VoiceResultDTO;
import aivle.ait.Entity.ContextResult;
import aivle.ait.Entity.VoiceResult;
import aivle.ait.Repository.ContextResultRepository;
import aivle.ait.Repository.FileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContextResultService {
    private final ObjectMapper objectMapper;
    private final ContextResultRepository contextResultRepository;
    private final FileRepository fileRepository;

    public ContextResultDTO readOne(Long id){
        Optional<ContextResult> contextResult = contextResultRepository.findById(id);
        if (contextResult.isEmpty()){
            return null;
        }
        ContextResultDTO contextResultDTO = new ContextResultDTO(contextResult.get());
        return contextResultDTO;
    }
}
