package aivle.ait.Controller;

import aivle.ait.Dto.ResultDTO;
import aivle.ait.Security.Auth.CustomUserDetails;
import aivle.ait.Service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/interviewGroup/{interviewGroup_id}/interviewer/{interviewer_id}/result", produces = MediaType.APPLICATION_JSON_VALUE)
public class ResultController {
    private final ResultService resultService;

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
                                       @PathVariable("interviewer_id") Long interviewer_id) throws ExecutionException, InterruptedException {
        ResultDTO resultDTO = resultService.analyze(interviewGroup_id, interviewer_id);
        if (resultDTO == null){
            System.out.println();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        return ResponseEntity.ok(resultDTO);
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
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(resultDTO);
    }
}
