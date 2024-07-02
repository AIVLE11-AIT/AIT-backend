package aivle.ait.Service;

import aivle.ait.Repository.ContextResultRepository;
import aivle.ait.Repository.FileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContextResultService {
    private final ObjectMapper objectMapper;
    private final ContextResultRepository contextResultRepository;
    private final FileRepository fileRepository;
}
