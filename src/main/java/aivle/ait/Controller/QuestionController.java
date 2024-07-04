package aivle.ait.Controller;

import aivle.ait.Dto.QuestionDTO;
import aivle.ait.Security.Auth.CustomUserDetails;
import aivle.ait.Service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/question", produces = MediaType.APPLICATION_JSON_VALUE)
public class QuestionController {

    private final QuestionService questionService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    public ResponseEntity<QuestionDTO> create(@RequestBody QuestionDTO questionDTO,
                                              @AuthenticationPrincipal CustomUserDetails customUserDetails){
        QuestionDTO createdQuestion = questionService.create(customUserDetails.getCompany().getId(), questionDTO);

        if (createdQuestion != null){
            return ResponseEntity.ok(createdQuestion);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/readAll")
    public ResponseEntity<List<QuestionDTO>> readAll(){
        List<QuestionDTO> questionDTOs = questionService.readAll();

        if (!questionDTOs.isEmpty()){
            return ResponseEntity.ok(questionDTOs);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 페이지 별로 불러오기 ex) localhost:8888/qustion/read?page=0&size=5
    @GetMapping("/read")
    public ResponseEntity<Page<QuestionDTO>> readPage(@PageableDefault(size = 5) Pageable pageable,
                                                      @AuthenticationPrincipal CustomUserDetails customUserDetails){
        Page<QuestionDTO> questionDTOs = questionService.readAllPageable(customUserDetails.getCompany().getId(), pageable);

        if (!questionDTOs.isEmpty()){
            return ResponseEntity.ok(questionDTOs);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 내 게시물만 불러오기
    @GetMapping("/readMyQuestion")
    public ResponseEntity<List<QuestionDTO>> readMyQuestion(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        List<QuestionDTO> questionDTOs = questionService.readMyQuestion(customUserDetails.getCompany().getId());

        if (!questionDTOs.isEmpty()){
            return ResponseEntity.ok(questionDTOs);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 해당 게시판이 내 것인지 확인
    @GetMapping("/{question_id}/check")
    public ResponseEntity<Boolean> checkMyBoard(@PathVariable Long question_id,
                                                @AuthenticationPrincipal CustomUserDetails customUserDetails){
        boolean isMine = questionService.checkMyQuestion(question_id, customUserDetails.getCompany().getName());

        if (isMine){
            return ResponseEntity.ok(isMine);
        }
        else{
            return ResponseEntity.badRequest().body(isMine);
        }
    }

    @PutMapping("/{question_id}/update")
    public ResponseEntity<QuestionDTO> update(@PathVariable Long question_id, @RequestBody QuestionDTO questionDTO){
        QuestionDTO updatedQuestions = questionService.update(question_id, questionDTO);

        if (updatedQuestions != null){
            return ResponseEntity.ok(updatedQuestions);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{question_id}")
    public ResponseEntity<QuestionDTO> read(@PathVariable Long question_id){
        QuestionDTO question_dto = questionService.readOne(question_id);

        if (question_dto != null){
            return ResponseEntity.ok(question_dto);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{question_id}/delete")
    public ResponseEntity<QuestionDTO> delete(@PathVariable Long question_id){
        QuestionDTO deletedQuestios = questionService.delete(question_id);

        if (deletedQuestios != null){
            return ResponseEntity.ok(deletedQuestios);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<QuestionDTO>> questionSearch(@PathVariable String keyword){
        List<QuestionDTO> questionDTOs = questionService.getQustionKeyword(keyword);

        if (!questionDTOs.isEmpty()){
            return ResponseEntity.ok(questionDTOs);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }
}
