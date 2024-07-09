package aivle.ait.Service;

import aivle.ait.Dto.*;
import aivle.ait.Entity.*;
import aivle.ait.Repository.*;
import aivle.ait.Util.RestAPIUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContextResultService {
    private final ObjectMapper objectMapper;
    private final ContextResultRepository contextResultRepository;
    private final CompanyQnaRepository companyQnaRepository;
    private final InterviewerQnaRepository interviewerQnaRepository;
    private final InterviewerRepository interviewerRepository;
    private final FileRepository fileRepository;

    @Value("${ait.server.contextServer}")
    private String baseUrl;

    public ContextResultDTO readOne(Long id){
        Optional<ContextResult> contextResult = contextResultRepository.findById(id);
        if (contextResult.isEmpty()){
            return null;
        }
        ContextResultDTO contextResultDTO = new ContextResultDTO(contextResult.get());
        return contextResultDTO;
    }

    // 기업 공통 질문 문장 처리
    @Async
    @Transactional
    public void sendToContextByCompanyQna(FileDTO fileDTO, Long companyQnaId, String interviewerAnswer) {
        // cosine_score, lsa_score, emotion_score, munmek_score, context_score 요청 후 값 받아오기
        String requestUrl = baseUrl + "/coQnaEval";

        Optional<CompanyQna> companyQna = companyQnaRepository.findById(companyQnaId);
        Optional<File> file = fileRepository.findById(fileDTO.getId());
        if (companyQna.isEmpty()){
            System.out.println("분석 단계 - 해당 companyQna이 없음");
            return;
        }
        if (file.isEmpty()){
            System.out.println("분석 단계 - 해당 file이 없음");
            return;
        }
        CompanyQnaDTO companyQnaDTO = new CompanyQnaDTO(companyQna.get());


        try {
            // body 생성
            Map<String, Object> body = new HashMap<>();
            body.put("question", companyQnaDTO.getQuestion());
            body.put("answer", interviewerAnswer);
            String response = RestAPIUtil.sendPostJson(requestUrl, body);

            // json 응답 파싱
            ContextResult contextResult = objectMapper.readValue(response, ContextResult.class);
            contextResult.setContext_score(70);
            contextResult.setMunmek_score(12);
            contextResult.setEmotion_score(20);

            // 연관 관계 설정
            contextResult.setFile(file.get());

            // save
            contextResultRepository.save(contextResult);
        }
        catch (JsonProcessingException e){
            e.printStackTrace();
        }
    }

    // 자소서 기반 질문 문장 처리
    @Async
    @Transactional
    public void sendToContextByInterviewerQna(FileDTO fileDTO, Long interviewerQnaId, Long interviewerId, String interviewerAnswer) {
        // 자소서와 답변간 cosine_score, lsa_score를 하고, 질문과 답변에 대해서 emotion_score, munmek_score, context_score를 분석
        String requestUrl = baseUrl + "/coverLetterEval";

        Optional<InterviewerQna> interviewerQna = interviewerQnaRepository.findById(interviewerQnaId);
        Optional<File> file = fileRepository.findById(fileDTO.getId());
        Optional<Interviewer> interviewer = interviewerRepository.findById(interviewerId);
        if (interviewerQna.isEmpty()){
            System.out.println("분석 단계 - 해당 interviewerQna이 없음");
            return;
        }
        if (file.isEmpty()){
            System.out.println("분석 단계 - 해당 file이 없음");
            return;
        }
        if (interviewer.isEmpty()){
            System.out.println("분석 단계 - 해당 interviewer이 없음");
            return;
        }
        InterviewerQnaDTO interviewerQnaDTO = new InterviewerQnaDTO(interviewerQna.get());
        InterviewerDTO interviewerDTO = new InterviewerDTO(interviewer.get());


        try {
            // body 생성
            Map<String, Object> body = new HashMap<>();
            body.put("cover_letter", interviewerDTO.getCover_letter());
            body.put("question", interviewerQnaDTO.getQuestion());
            body.put("answer", interviewerAnswer);
            String response = RestAPIUtil.sendPostJson(requestUrl, body);

            // json 응답 파싱
            ContextResult contextResult = objectMapper.readValue(response, ContextResult.class);

            // 연관 관계 설정
            contextResult.setFile(file.get());

            // save
            contextResultRepository.save(contextResult);
        }
        catch (JsonProcessingException e){
            e.printStackTrace();
        }
    }
}
