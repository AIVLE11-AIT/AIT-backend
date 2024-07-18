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
    public ResponseEntity<?> create(@PathVariable("question_id") Long question_id, @RequestBody AnswerDTO answer_dto){
        try{
            AnswerDTO created_answer = answerService.create(question_id, answer_dto);

            if (created_answer != null){
                return ResponseEntity.ok(created_answer);
            }
            else{
                return ResponseEntity.badRequest().body("question_id값이 유효하지 않음.");
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/read")
    public ResponseEntity<?> readOne(@PathVariable("question_id") Long question_id){
        try{
            AnswerDTO answerDTO = answerService.readOne(question_id);

            if (answerDTO != null){
                return ResponseEntity.ok(answerDTO);
            }
            else{
                return ResponseEntity.badRequest().body("question_id가 유효하지 않음.");
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/update/{answerId}")
    public ResponseEntity<?> update(@PathVariable("answerId") Long answerId, @RequestBody Answer answer){
        try{
            AnswerDTO updatedComment = answerService.update(answerId, answer.getContent());

            if (updatedComment != null){
                return ResponseEntity.ok(updatedComment);
            }
            else{
                return ResponseEntity.badRequest().body("answerId가 유효하지 않음.");
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{answerId}")
    public ResponseEntity<?> delete(@PathVariable("answerId") Long answerId){
        try{
            AnswerDTO deletedComment = answerService.delete(answerId);

            if (deletedComment != null){
                return ResponseEntity.ok(deletedComment);
            }
            else{
                return ResponseEntity.badRequest().body("answerId가 유효하지 않음.");
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

    }
}
