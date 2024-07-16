package aivle.ait.Service;

import aivle.ait.Dto.InterviewerQnaDTO;
import aivle.ait.Entity.Interviewer;
import aivle.ait.Entity.InterviewerQna;
import aivle.ait.Repository.InterviewerQnaRepository;
import aivle.ait.Repository.InterviewerRepository;
import aivle.ait.Util.RestAPIUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InterviewerQnaService {
    private final InterviewerQnaRepository interviewerQnaRepository;
    private final InterviewerRepository interviewerRepository;

    @Value("${ait.server.createQuestionServer}")
    private String baseUrl;

    @Transactional
    @Async
    public void create(Interviewer interviewer) {
        String llmUrl = baseUrl + "/personal_question";

        try {
            String cover_letter = interviewer.getCover_letter();

            if (cover_letter == null || cover_letter == "") {
                System.out.println("인터뷰 자소서 기반 질문 생성 에러");
                return;
            }

            // body 생성
            Map<String, Object> body = new HashMap<>();
            body.put("cover_letter", cover_letter);

            // send
            String responseBody = RestAPIUtil.sendPostJson(llmUrl, body);

            // response 처리
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode questionsNode = root.get("questions"); // "questions" 배열 노드 가져오기

            if (questionsNode.isArray()) {
                for (JsonNode questionNode : questionsNode) {
                    String question = questionNode.get("question").asText(); // 각 질문의 "question" 필드 가져오기

                    // 저장
                    InterviewerQna interviewerQna = new InterviewerQna();
                    interviewerQna.setQuestion(question);
                    interviewerQna.setInterviewer(interviewer);
                    interviewerQnaRepository.save(interviewerQna);
                }
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public InterviewerQnaDTO readOne(Long interviewerQnaId, Long interviewerId, Long interviewGroupId){
        Optional<InterviewerQna> interviewerQnas = interviewerQnaRepository.findInterviewerByIdAndInterviewerId(interviewerQnaId, interviewerId);
        if (interviewerQnas.isEmpty() || interviewerQnas.get().getInterviewer().getInterviewgroup().getId() != interviewGroupId) {
            return null;
        }

        InterviewerQnaDTO interviewerQnaDTO = new InterviewerQnaDTO(interviewerQnas.get());
        return interviewerQnaDTO;
    }

    public List<InterviewerQnaDTO> readAll(Long interviewerId, Long interviewGroupId){
        Optional<Interviewer> interviewers = interviewerRepository.findById(interviewerId);
        if (interviewers.isEmpty() || interviewers.get().getInterviewgroup().getId() != interviewGroupId) {
            return null;
        }
        List<InterviewerQnaDTO> interviewerQnaDTOS = InterviewerQnaDTO.convertToDto(interviewers.get().getInterviewerQnas());
        return interviewerQnaDTOS;
    }
}
