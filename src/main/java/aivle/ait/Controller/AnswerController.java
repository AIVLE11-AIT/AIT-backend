package aivle.ait.Controller;

import aivle.ait.Dto.AnswerDTO;
import aivle.ait.Entity.Answer;
import aivle.ait.Service.AnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/question/{question_id}/answer", produces = MediaType.APPLICATION_JSON_VALUE)
public class AnswerController {

    private final AnswerService answerService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    public ResponseEntity<AnswerDTO> create(@PathVariable("question_id") Long question_id, @RequestBody AnswerDTO answer_dto){
        AnswerDTO created_answer = answerService.create(question_id, answer_dto);

        if (created_answer != null){
            return ResponseEntity.ok(created_answer);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/read")
    public ResponseEntity<String> readAll(@PathVariable("question_id") Long question_id){
        String answer = answerService.readOne(question_id);

        if (answer != null){
            return ResponseEntity.ok(answer);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/update/{commentId}")
    public ResponseEntity<AnswerDTO> update(@PathVariable("commentId") Long commentId, @RequestBody Answer answer){
        AnswerDTO updatedComment = answerService.update(commentId, answer.getContent());

        if (updatedComment != null){
            return ResponseEntity.ok(updatedComment);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<AnswerDTO> delete(@PathVariable("commentId") Long commentId){
        AnswerDTO deletedComment = answerService.delete(commentId);

        if (deletedComment != null){
            return ResponseEntity.ok(deletedComment);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }
}
