package aivle.ait.Controller;

import aivle.ait.Dto.*;
import aivle.ait.Entity.InterviewGroup;
import aivle.ait.Security.Auth.CustomUserDetails;
import aivle.ait.Service.*;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.descriptor.web.ContextService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/interviewGroup/{interviewGroup_id}/interviewer/{interviewer_id}/result", produces = MediaType.APPLICATION_JSON_VALUE)
public class ResultController {
    private final InterviewGroupService interviewGroupService;
    private final InterviewerService interviewerService;
    private final ActionResultService actionResultService;
    private final VoiceResultService voiceResultService;
    private final ContextResultService contextResultService;

    @GetMapping("/finish")
    public ResponseEntity<?> analyze(@PathVariable("interviewGroup_id") Long interviewGroup_id,
                                               @PathVariable("interviewer_id") Long interviewer_id,
                                               @AuthenticationPrincipal CustomUserDetails customUserDetails){
        InterviewGroupDTO interviewGroupDTO = interviewGroupService.readOne(customUserDetails.getCompany().getId(), interviewGroup_id);
        if (interviewGroupDTO == null){
            return ResponseEntity.badRequest().body("해당 인터뷰 그룹이 없음.");
        }
        InterviewerDTO interviewerDTO = interviewerService.readOne(customUserDetails.getCompany().getId(), interviewGroup_id, interviewer_id);
        if (interviewerDTO == null){
            return ResponseEntity.badRequest().body("해당 인터뷰어가 없음.");
        }

        // 행동 분석
        double total_action_score = 0;
        for (FileDTO fileDTO : interviewerDTO.getFiles()) {
            ActionResultDTO actionResultDTO = actionResultService.readOne(fileDTO.getAction_result_id());
            if (actionResultDTO == null) continue;

            total_action_score += actionResultDTO.getAction_score();
        }
        total_action_score /= interviewerDTO.getFiles().size();


        // 음성 분석
        double total_voice_score = 0;
        for (FileDTO fileDTO : interviewerDTO.getFiles()) {
            VoiceResultDTO voiceResultDTO = voiceResultService.readOne(fileDTO.getVoice_result_id());
            if (voiceResultDTO == null) continue;

            total_voice_score += voiceResultDTO.getVoice_score();
        }
        total_voice_score /= interviewerDTO.getFiles().size();

        // 문맥 분석
        double total_context_score = 0;
        for (FileDTO fileDTO : interviewerDTO.getFiles()) {
            ContextResultDTO contextResultDTO = contextResultService.readOne(fileDTO.getContext_result_id());
            if (contextResultDTO == null) continue;

            total_context_score += contextResultDTO.getContext_score();
        }
        total_context_score /= interviewerDTO.getFiles().size();

        // 3개의 평가항목을 100점 만점으로 변환
        int total_score = (int)calculateWeightedScore(total_action_score, total_voice_score, total_context_score,
                (double) interviewGroupDTO.getAction_per() / 100, (double) interviewGroupDTO.getVoice_per() / 100, (double) interviewGroupDTO.getContext_per() / 100);

        // llm을 사용한 총 평가 (total_report) - 해야함...
        
        
        // DB 저장

        return ResponseEntity.ok("미완성");
    }

    public double calculateWeightedScore(double scoreA, double scoreB, double scoreC,
                                                double weightA, double weightB, double weightC) {
        return (scoreA * weightA) + (scoreB * weightB) + (scoreC * weightC);
    }
}
