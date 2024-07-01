package aivle.ait.Controller;

import aivle.ait.Dto.InterviewerDTO;
import aivle.ait.Security.Auth.CustomUserDetails;
import aivle.ait.Service.InterviewerService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
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
@RequestMapping(value = "/interviewGroup/{interviewGroup_id}/interviewer", produces = MediaType.APPLICATION_JSON_VALUE)
public class InterviewerController {
    private final InterviewerService interviewerService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    public ResponseEntity<InterviewerDTO> create(@RequestBody InterviewerDTO interviewerDTO,
                                                  @PathVariable("interviewGroup_id") Long interviewGroup_id,
                                                  @AuthenticationPrincipal CustomUserDetails customUserDetails){
        InterviewerDTO createdInterviewerDTO = interviewerService.create(customUserDetails.getCompany().getId(), interviewGroup_id, interviewerDTO);

        if (createdInterviewerDTO != null){
            return ResponseEntity.ok(createdInterviewerDTO);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{interviewer_id}")
    public ResponseEntity<InterviewerDTO> read(@PathVariable("interviewGroup_id") Long interviewGroup_id,
                                                @PathVariable("interviewer_id") Long preInterview_id,
                                                @AuthenticationPrincipal CustomUserDetails customUserDetails){
        InterviewerDTO InterviewerDTO = interviewerService.readOne(customUserDetails.getCompany().getId(), interviewGroup_id, preInterview_id);

        if (InterviewerDTO != null){
            return ResponseEntity.ok(InterviewerDTO);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/readAll")
    public ResponseEntity<List<InterviewerDTO>> readAll(@PathVariable("interviewGroup_id") Long interviewGroup_id,
                                                         @AuthenticationPrincipal CustomUserDetails customUserDetails){
        List<InterviewerDTO> InterviewerDTOS = interviewerService.readAll(customUserDetails.getCompany().getId(), interviewGroup_id);

        if (InterviewerDTOS != null){
            return ResponseEntity.ok(InterviewerDTOS);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 내 면접 그룹들만 페이지 별로 불러오기 ex) localhost:8888/interviewGroup/{interviewGroup_id}/preInterview/read?page=0&size=5
    @GetMapping("/read")
    public ResponseEntity<Page<InterviewerDTO>> readPage(@PageableDefault(size = 5) Pageable pageable,
                                                          @PathVariable("interviewGroup_id") Long interviewGroup_id,
                                                          @AuthenticationPrincipal CustomUserDetails customUserDetails){
        Page<InterviewerDTO> InterviewerDTOS = interviewerService.readAllPageable(customUserDetails.getCompany().getId(), interviewGroup_id, pageable);

        if (!InterviewerDTOS.isEmpty()){
            return ResponseEntity.ok(InterviewerDTOS);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{interviewer_id}/update")
    public ResponseEntity<InterviewerDTO> update(@PathVariable("interviewGroup_id") Long interviewGroup_id,
                                                  @PathVariable("interviewer_id") Long preInterview_id,
                                                  @RequestBody InterviewerDTO interviewerDTO,
                                                  @AuthenticationPrincipal CustomUserDetails customUserDetails){
        InterviewerDTO updatedInterviewerDTO = interviewerService.update(customUserDetails.getCompany().getId(), interviewGroup_id, preInterview_id, interviewerDTO);

        if (updatedInterviewerDTO != null){
            return ResponseEntity.ok(updatedInterviewerDTO);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{interviewer_id}/delete")
    public ResponseEntity<InterviewerDTO> delete(@PathVariable("interviewGroup_id") Long interviewGroup_id,
                                                  @PathVariable("interviewer_id") Long preInterview_id,
                                                  @AuthenticationPrincipal CustomUserDetails customUserDetails){
        InterviewerDTO deletedInterviewerDTO = interviewerService.delete(customUserDetails.getCompany().getId(), interviewGroup_id, preInterview_id);

        if (deletedInterviewerDTO != null){
            return ResponseEntity.ok(deletedInterviewerDTO);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 지원자에게 면접 링크 메일 전송
    @GetMapping("/{interviewer_id}/send")
    public ResponseEntity<InterviewerDTO> sendEmail(@PathVariable("interviewGroup_id") Long interviewGroup_id,
                                               @PathVariable("interviewer_id") Long preInterview_id,
                                               @AuthenticationPrincipal CustomUserDetails customUserDetails){
        InterviewerDTO InterviewerDTO = interviewerService.readOne(customUserDetails.getCompany().getId(), interviewGroup_id, preInterview_id);

        if (InterviewerDTO != null){
            String url = "http://localhost:8080/" + customUserDetails.getCompany().getId() + "/" + interviewGroup_id + "/" + preInterview_id;
            try {
                System.out.println(customUserDetails.getCompany().getName());
                interviewerService.sendEmail(InterviewerDTO, InterviewerDTO.getInterview_group(), url);
                return ResponseEntity.ok(InterviewerDTO);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(InterviewerDTO);
            }
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }
}
