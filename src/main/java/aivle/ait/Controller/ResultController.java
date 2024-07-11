package aivle.ait.Controller;

import aivle.ait.Service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/interviewGroup/{interviewGroup_id}/interviewer/{interviewer_id}/result", produces = MediaType.APPLICATION_JSON_VALUE)
public class ResultController {
    private final ResultService resultService;

    @GetMapping("/finish")
    public ResponseEntity<?> analyze(@PathVariable("interviewGroup_id") Long interviewGroup_id,
                                       @PathVariable("interviewer_id") Long interviewer_id){
        resultService.analyze(interviewGroup_id, interviewer_id);

        return ResponseEntity.ok("미완성");
    }

//    // 결과 레포트 시각화
//    @GetMapping("/visualize")
//    public ResponseEntity<?> visualize(@PathVariable("interviewGroup_id") Long interviewGroup_id,
//                                       @PathVariable("interviewer_id") Long interviewer_id,
//                                       @AuthenticationPrincipal CustomUserDetails customUserDetails) {
//        // response
//        // 면접 문항
//        //
//    }
}
