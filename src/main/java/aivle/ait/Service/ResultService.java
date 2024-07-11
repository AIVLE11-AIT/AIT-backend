package aivle.ait.Service;

import aivle.ait.Dto.*;
import aivle.ait.Entity.*;
import aivle.ait.Repository.*;
import aivle.ait.Util.RestAPIUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResultService {
    private final InterviewGroupRepository interviewGroupRepository;
    private final InterviewerRepository interviewerRepository;
    private final ActionResultRepository actionResultRepository;
    private final VoiceResultRepository voiceResultRepository;
    private final ContextResultRepository contextResultRepository;
    private final ResultRepository resultRepository;

    @Value("${ait.server.reportServer}")
    private String baseUrl;

    @Async
    @Transactional
    public ResultDTO analyze(Long interviewGroupId, Long interviewerId){
        Optional<InterviewGroup> interviewGroups = interviewGroupRepository.findById(interviewGroupId);
        if (interviewGroups.isEmpty()){
            System.out.println("해당 인터뷰 그룹이 없음.");
            return null;
        }
        InterviewGroupDTO interviewGroupDTO = new InterviewGroupDTO(interviewGroups.get());

        Optional<Interviewer> interviewers = interviewerRepository.findInterviewerByIdAndInterviewgroupId(interviewerId, interviewGroupId);
        if (interviewers.isEmpty()){
            System.out.println("해당 인터뷰어가 없음.");
            return null;
        }
        Interviewer interviewer = interviewers.get();
        InterviewerDTO interviewerDTO = new InterviewerDTO(interviewer);

        // result 생성
        Result result = new Result();
        result.setZero(); // score값들을 모두 0으로 초기화
        result.setInterviewer(interviewer); // 관계 설정
        int questionCount = interviewerDTO.getFiles().size();
        if (questionCount == 0){
            System.out.println("연관된 질문이 없음.");
            return null;
        }

        // 행동 분석
        for (FileDTO fileDTO : interviewerDTO.getFiles()) {
            Optional<ActionResult> actionResults = actionResultRepository.findById(fileDTO.getAction_result_id());
            if (actionResults.isEmpty()) continue;

            ActionResult actionResult = actionResults.get();
            result.setTotal_face_gesture_score(result.getTotal_face_gesture_score() + (int)actionResult.getFace_gesture_score());
            result.setTotal_eyetrack_gesture_score(result.getTotal_eyetrack_gesture_score() + (int)actionResult.getEyetrack_gesture_score());
            result.setTotal_body_gesture_score(result.getTotal_body_gesture_score() + (int)actionResult.getBody_gesture_score());
            result.setTotal_hand_count_score(result.getTotal_hand_count_score() + (int)actionResult.getHand_count_score());
            result.setTotal_emotion_score(result.getTotal_emotion_score() + (int)actionResult.getEmotion_score());
            result.setAction_score(result.getAction_score() + (int)actionResult.getAction_score());

            // 관계 설정
            actionResult.setResult(result);
        }
        result.setTotal_face_gesture_score(result.getTotal_face_gesture_score() / questionCount);
        result.setTotal_eyetrack_gesture_score(result.getTotal_eyetrack_gesture_score() / questionCount);
        result.setTotal_body_gesture_score(result.getTotal_body_gesture_score() / questionCount);
        result.setTotal_hand_count_score(result.getTotal_hand_count_score() / questionCount);
        result.setTotal_emotion_score(result.getTotal_emotion_score() / questionCount);
        result.setAction_score(result.getAction_score() / questionCount);

        // 음성 분석
        for (FileDTO fileDTO : interviewerDTO.getFiles()) {
            Optional<VoiceResult> voiceResults = voiceResultRepository.findById(fileDTO.getVoice_result_id());
            if (voiceResults.isEmpty()) continue;

            VoiceResult voiceResult = voiceResults.get();
            result.setTotal_voice_level(result.getTotal_voice_level() + (int)voiceResult.getVoice_level());
            result.setTotal_voice_speed(result.getTotal_voice_speed() + (int)voiceResult.getVoice_speed());
            result.setTotal_voice_intj(result.getTotal_voice_intj() + (int)voiceResult.getVoice_intj());
            result.setVoice_score(result.getVoice_score() + (int)voiceResult.getVoice_score());

            // 관계 설정
            voiceResult.setResult(result);
        }
        result.setTotal_voice_level(result.getTotal_voice_level() / questionCount);
        result.setTotal_voice_speed(result.getTotal_voice_speed() / questionCount);
        result.setTotal_voice_intj(result.getTotal_voice_intj() / questionCount);
        result.setVoice_score(result.getVoice_score() / questionCount);

        // 문맥 분석
        List<Map<String, String>> answerValuation = new ArrayList<>();
        for (FileDTO fileDTO : interviewerDTO.getFiles()) {
            Optional<ContextResult> contextResults = contextResultRepository.findById(fileDTO.getContext_result_id());
            if (contextResults.isEmpty()) continue;

            ContextResult contextResult = contextResults.get();
            result.setTotal_similarity_score(result.getTotal_similarity_score() + (int)contextResult.getSimilarity_score());
            result.setTotal_lsa_score(result.getTotal_lsa_score() + (int)contextResult.getLsa_score());
            result.setTotal_emotion_score(result.getTotal_emotion_score() + (int)contextResult.getEmotion_score());
            result.setTotal_munmek_score(result.getTotal_munmek_score() + (int)contextResult.getMunmek_score());
            result.setContext_score(result.getContext_score() + (int)contextResult.getContext_score());

            // Detail 저장
            answerValuation.add(
                    Map.of(
                            "Relevance", contextResult.getMunmekDetail().getRelevance(),
                            "Logicality", contextResult.getMunmekDetail().getLogicality(),
                            "Clarity", contextResult.getMunmekDetail().getClarity(),
                            "Question Comprehension", contextResult.getMunmekDetail().getQuestionComprehension()
                    )
            );

            // 관계 설정
            contextResult.setResult(result);
        }
        result.setTotal_similarity_score(result.getTotal_similarity_score() / questionCount);
        result.setTotal_lsa_score(result.getTotal_lsa_score() / questionCount);
        result.setTotal_emotion_score(result.getTotal_emotion_score() / questionCount);
        result.setContext_score(result.getContext_score() / questionCount);

        // 3개의 평가항목을 100점 만점으로 변환
        int total_score = (int)calculateWeightedScore(result.getAction_score(), result.getVoice_score(), result.getContext_score(),
                (double) interviewGroupDTO.getAction_per() / 100, (double) interviewGroupDTO.getVoice_per() / 100, (double) interviewGroupDTO.getContext_per() / 100);
        result.setTotal_score(total_score);

        // llm을 사용한 총 평가 (total_report)
        createReport(result, interviewGroupDTO, answerValuation);

        // DB 저장
        resultRepository.save(result);

        return new ResultDTO(result);
    }

    public String createReport(Result result, InterviewGroupDTO interviewGroupDTO, List<Map<String, String>> answerValuation){
        String requestUrl = baseUrl + "/report";
        try{
            // body 생성
            Map<String, Object> body = new HashMap<>();

            // job_position 생성
            Map<String, String> jobPosition = new HashMap<>();
            jobPosition.put("Job", interviewGroupDTO.getOccupation());
            body.put("job_position", jobPosition);

            // interview_Score 생성
            Map<String, Object> interviewScore = new HashMap<>();

            Map<String, Integer> actionScores = new HashMap<>();
            actionScores.put("face_gesture_score", result.getTotal_face_gesture_score());
            actionScores.put("eyetrack_gesture_score", result.getTotal_eyetrack_gesture_score());
            actionScores.put("body_gesture_score", result.getTotal_body_gesture_score());
            actionScores.put("hand_count_score", result.getTotal_hand_count_score());
            actionScores.put("emotion_score", result.getTotal_emotion_score());
            actionScores.put("action_score", result.getAction_score());
            interviewScore.put("Action", actionScores);

            Map<String, Integer> voiceScores = new HashMap<>();
            voiceScores.put("voice_level", result.getTotal_voice_level());
            voiceScores.put("voice_speed", result.getTotal_voice_speed());
            voiceScores.put("voice_intj", result.getTotal_voice_intj());
            voiceScores.put("voice_score", result.getVoice_score());
            interviewScore.put("Voice", voiceScores);

            Map<String, Integer> contextScores = new HashMap<>();
            contextScores.put("lsa_score", result.getTotal_lsa_score());
            contextScores.put("similarity_score", result.getTotal_similarity_score());
            contextScores.put("emotion_score", result.getTotal_emotion_score());
            contextScores.put("munmek_score", result.getTotal_munmek_score());
            contextScores.put("context_score", result.getContext_score());
            interviewScore.put("Context", contextScores);

            body.put("interview_Score", interviewScore);

            // answer_valuation 생성
            body.put("answer_valuation", answerValuation);

            String response = RestAPIUtil.sendPostJson(requestUrl, body);

            return "";
        }
        catch (Exception e){
            e.printStackTrace();
            return "createReport Error";
        }
    }

    public double calculateWeightedScore(double scoreA, double scoreB, double scoreC,
                                         double weightA, double weightB, double weightC) {
        return (scoreA * weightA) + (scoreB * weightB) + (scoreC * weightC);
    }
}
