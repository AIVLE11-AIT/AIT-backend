package aivle.ait.Controller;

import aivle.ait.Dto.InterviewerQnaDTO;
import aivle.ait.Service.InterviewerQnaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/interviewGroup/{interviewGroup_id}/{interviewer_id}/interviewerQna", produces = MediaType.APPLICATION_JSON_VALUE)
public class InterviewerQnaController {
    private final InterviewerQnaService interviewerQnaService;

    @GetMapping("/{interviewerQna_id}")
    public ResponseEntity<?> read(@PathVariable("interviewGroup_id") Long interviewGroup_id,
                                              @PathVariable("interviewer_id") Long interviewer_id,
                                              @PathVariable("interviewerQna_id") Long interviewerQna_id){
        InterviewerQnaDTO interviewerQnaDTO = interviewerQnaService.readOne(interviewerQna_id, interviewer_id, interviewGroup_id);

        if (interviewerQnaDTO != null){
            return ResponseEntity.ok(interviewerQnaDTO);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/readAll")
    public ResponseEntity<?> readAll(@PathVariable("interviewGroup_id") Long interviewGroup_id,
                                     @PathVariable("interviewer_id") Long interviewer_id){
        List<InterviewerQnaDTO> interviewerQnaDTOS = interviewerQnaService.readAll(interviewer_id, interviewGroup_id);

        if (interviewerQnaDTOS != null){
            return ResponseEntity.ok(interviewerQnaDTOS);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }
}
