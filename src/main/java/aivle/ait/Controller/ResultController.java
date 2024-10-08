package aivle.ait.Controller;

import aivle.ait.Dto.InterviewGroupDTO;
import aivle.ait.Dto.ResultDTO;
import aivle.ait.Entity.InterviewGroup;
import aivle.ait.Entity.Interviewer;
import aivle.ait.Repository.InterviewGroupRepository;
import aivle.ait.Repository.InterviewerRepository;
import aivle.ait.Security.Auth.CustomUserDetails;
import aivle.ait.Service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/interviewGroup/{interviewGroup_id}/interviewer/{interviewer_id}/result", produces = MediaType.APPLICATION_JSON_VALUE)
public class ResultController {
    private final ResultService resultService;
    private final InterviewGroupRepository interviewGroupRepository;
    private final InterviewerRepository interviewerRepository;

    // 테스트할 때 지우는 용
    @GetMapping("/delete/{result_id}")
    public ResponseEntity<?> delete(@PathVariable("result_id") Long result_id) {
        ResultDTO resultDTO = resultService.delete(result_id);
        if (resultDTO == null){
            System.out.println();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        return ResponseEntity.ok(resultDTO);
    }

    @GetMapping("/finish")
    public ResponseEntity<?> analyze(@PathVariable("interviewGroup_id") Long interviewGroup_id,
                                       @PathVariable("interviewer_id") Long interviewer_id){
        try{
            Optional<InterviewGroup> interviewGroupOptional = interviewGroupRepository.findById(interviewGroup_id);
            Optional<Interviewer> interviewerOptional = interviewerRepository.findInterviewerByIdAndInterviewgroupId(interviewer_id, interviewGroup_id);
            if (interviewGroupOptional.isEmpty() || interviewerOptional.isEmpty()) {
                return ResponseEntity.badRequest().body("인터뷰 그룹이 없음 or 인터뷰어 없음");
            }
            InterviewGroup interviewGroup = interviewGroupOptional.get();
            InterviewGroupDTO interviewGroupDTO = new InterviewGroupDTO(interviewGroup);
            return ResponseEntity.ok(interviewGroupDTO);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 결과 레포트 시각화
    @GetMapping("/read")
    public ResponseEntity<?> read(@PathVariable("interviewer_id") Long interviewer_id,
                                   @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        // response
        // 면접 문항
        ResultDTO resultDTO = resultService.read(interviewer_id, customUserDetails.getCompany().getId());
        if (resultDTO == null) {
            System.out.println("result 없음");
            return ResponseEntity.internalServerError().body("result 없음");
        }
        else {
            return ResponseEntity.ok(resultDTO);
        }
    }
}
