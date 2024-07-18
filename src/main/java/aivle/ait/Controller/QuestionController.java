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

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/question", produces = MediaType.APPLICATION_JSON_VALUE)
public class QuestionController {

    private final QuestionService questionService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody QuestionDTO questionDTO,
                                              @AuthenticationPrincipal CustomUserDetails customUserDetails){
        try{
            QuestionDTO createdQuestion = questionService.create(customUserDetails.getCompany().getId(), questionDTO);

            if (createdQuestion != null){
                return ResponseEntity.ok(createdQuestion);
            }
            else{
                return ResponseEntity.badRequest().body("로그인 확인 필요.");
            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/readAll")
    public ResponseEntity<?> readAll(){
        try{
            List<QuestionDTO> questionDTOs = questionService.readAll();

            if (!questionDTOs.isEmpty()){
                return ResponseEntity.ok(questionDTOs);
            }
            else{
                return ResponseEntity.badRequest().body("question이 없음.");
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    // 페이지 별로 불러오기 ex) localhost:8888/qustion/read?page=0&size=5
    @GetMapping("/read")
    public ResponseEntity<?> readPage(@PageableDefault(size = 5) Pageable pageable,
                                                      @AuthenticationPrincipal CustomUserDetails customUserDetails){
        try{
            Page<QuestionDTO> questionDTOs = questionService.readAllPageable(customUserDetails.getCompany().getId(), pageable);

            if (!questionDTOs.isEmpty()){
                return ResponseEntity.ok(questionDTOs);
            }
            else{
                return ResponseEntity.badRequest().body("question이 없음.");
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    // 내 게시물만 불러오기
    @GetMapping("/readMyQuestion")
    public ResponseEntity<?> readMyQuestion(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        try{
            List<QuestionDTO> questionDTOs = questionService.readMyQuestion(customUserDetails.getCompany().getId());

            if (questionDTOs != null){
                return ResponseEntity.ok(questionDTOs);
            }
            else{
                return ResponseEntity.badRequest().body("로그인 확인 필요");
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

    }

    // 해당 게시판이 내 것인지 확인
    @GetMapping("/{question_id}/check")
    public ResponseEntity<?> checkMyBoard(@PathVariable Long question_id,
                                                @AuthenticationPrincipal CustomUserDetails customUserDetails){
        try{
            boolean isMine = questionService.checkMyQuestion(question_id, customUserDetails.getCompany().getName());

            if (isMine){
                return ResponseEntity.ok(isMine);
            }
            else{
                return ResponseEntity.badRequest().body(isMine);
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

    }

    @PutMapping("/{question_id}/update")
    public ResponseEntity<?> update(@PathVariable Long question_id, @RequestBody QuestionDTO questionDTO){
        try{
            QuestionDTO updatedQuestions = questionService.update(question_id, questionDTO);

            if (updatedQuestions != null){
                return ResponseEntity.ok(updatedQuestions);
            }
            else{
                return ResponseEntity.badRequest().body("해당 question이 없음.");
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

    }

    @GetMapping("/{question_id}")
    public ResponseEntity<?> read(@PathVariable Long question_id){
        try{
            QuestionDTO question_dto = questionService.readOne(question_id);

            if (question_dto != null){
                return ResponseEntity.ok(question_dto);
            }
            else{
                return ResponseEntity.badRequest().body("question이 존재하지 않음.");
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

    }

    @DeleteMapping("/{question_id}/delete")
    public ResponseEntity<?> delete(@PathVariable Long question_id){
        try{
            QuestionDTO deletedQuestios = questionService.delete(question_id);

            if (deletedQuestios != null){
                return ResponseEntity.ok(deletedQuestios);
            }
            else{
                return ResponseEntity.badRequest().body("question이 존재하지 않음.");
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<QuestionDTO>> questionSearch(@PathVariable String keyword){
        List<QuestionDTO> questionDTOs = questionService.getQustionKeyword(keyword);

        return ResponseEntity.ok(questionDTOs);
    }
}
