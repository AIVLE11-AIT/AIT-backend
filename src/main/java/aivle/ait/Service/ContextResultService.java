package aivle.ait.Service;

import aivle.ait.Dto.*;
import aivle.ait.Entity.*;
import aivle.ait.Repository.*;
import aivle.ait.Util.RestAPIUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final InterviewGroupRepository interviewGroupRepository;

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
    public void sendToContextByCompanyQna(FileDTO fileDTO, Long companyQnaId, String interviewerAnswer, Long interviewGroup_id) {
        // cosine_score, lsa_score, emotion_score, munmek_score, context_score 요청 후 값 받아오기
        String requestUrl = baseUrl + "/coQnaEval";

        Optional<CompanyQna> companyQnas = companyQnaRepository.findById(companyQnaId);
        Optional<File> file = fileRepository.findById(fileDTO.getId());
        Optional<InterviewGroup> interviewGroup = interviewGroupRepository.findById(interviewGroup_id);
        if (companyQnas.isEmpty()){
            System.out.println("분석 단계 - 해당 companyQna이 없음");
            return;
        }
        if (file.isEmpty()){
            System.out.println("분석 단계 - 해당 file이 없음");
            return;
        }
        if (interviewGroup.isEmpty()){
            System.out.println("분석 단계 - 해당 interviewGroup이 없음");
            return;
        }

        // 답변 저장 추가
        CompanyQna companyQna = companyQnas.get();
        companyQna.setAnswer(interviewerAnswer);

        CompanyQnaDTO companyQnaDTO = new CompanyQnaDTO(companyQna);
        InterviewGroupDTO interviewGroupDTO = new InterviewGroupDTO(interviewGroup.get());


        try {
            // body 생성
            Map<String, Object> body = new HashMap<>();
            body.put("occupation", interviewGroupDTO.getOccupation());
            body.put("question", companyQnaDTO.getQuestion());
            body.put("answer", interviewerAnswer);
            String response = RestAPIUtil.sendPostJson(requestUrl, body);

            // json 응답 파싱
            JsonNode jsonResponse = objectMapper.readTree(response);
            ContextResult contextResult = new ContextResult();
            contextResult.setContext_score(jsonResponse.get("context_score").asDouble());
            contextResult.setMunmek_score(jsonResponse.get("munmek_score").asDouble());
            contextResult.setSimilarity_score(jsonResponse.get("similarity_score").asDouble());
            contextResult.setLsa_score(jsonResponse.get("lsa_score").asDouble());
            contextResult.setEmotion_score(jsonResponse.get("emotion_score").asDouble());

            // details 저장
            JsonNode munmekNode = jsonResponse.path("munmek");
            MunmekDetail munmekDetail = new MunmekDetail();
            munmekDetail.setClarity(munmekNode.get("Clarity").asText());
            munmekDetail.setLogicality(munmekNode.get("Logicality").asText());
            munmekDetail.setQuestionComprehension(munmekNode.get("Question Comprehension").asText());
            munmekDetail.setRelevance(munmekNode.get("Relevance").asText());

            // 연관 관계 설정
            contextResult.setFile(file.get());
            munmekDetail.setContextResult(contextResult);

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

        Optional<InterviewerQna> interviewerQnas = interviewerQnaRepository.findById(interviewerQnaId);
        Optional<File> file = fileRepository.findById(fileDTO.getId());
        Optional<Interviewer> interviewer = interviewerRepository.findById(interviewerId);
        if (interviewerQnas.isEmpty()){
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

        // 답변 저장 추가
        InterviewerQna interviewerQna = interviewerQnas.get();
        interviewerQna.setAnswer(interviewerAnswer);

        InterviewerQnaDTO interviewerQnaDTO = new InterviewerQnaDTO(interviewerQna);
        InterviewerDTO interviewerDTO = new InterviewerDTO(interviewer.get());

        try {
            // body 생성
            Map<String, Object> body = new HashMap<>();
            body.put("cover_letter", interviewerDTO.getCover_letter());
            body.put("question", interviewerQnaDTO.getQuestion());
            body.put("answer", interviewerAnswer);
            String response = RestAPIUtil.sendPostJson(requestUrl, body);

            // json 응답 파싱
            JsonNode jsonResponse = objectMapper.readTree(response);
            ContextResult contextResult = new ContextResult();
            contextResult.setContext_score(jsonResponse.get("context_score").asDouble());
            contextResult.setMunmek_score(jsonResponse.get("munmek_score").asDouble());
            contextResult.setSimilarity_score(jsonResponse.get("similarity_score").asDouble());
            contextResult.setLsa_score(jsonResponse.get("lsa_score").asDouble());
            contextResult.setEmotion_score(20);

            // details 저장
            JsonNode munmekNode = jsonResponse.path("munmek");
            MunmekDetail munmekDetail = new MunmekDetail();
            munmekDetail.setClarity(munmekNode.get("Clarity").asText());
            munmekDetail.setLogicality(munmekNode.get("Logicality").asText());
            munmekDetail.setQuestionComprehension(munmekNode.get("Question Comprehension").asText());
            munmekDetail.setRelevance(munmekNode.get("Relevance").asText());

            // 연관 관계 설정
            contextResult.setFile(file.get());
            munmekDetail.setContextResult(contextResult);

            // save
            contextResultRepository.save(contextResult);
        }
        catch (JsonProcessingException e){
            e.printStackTrace();
        }
    }
}
