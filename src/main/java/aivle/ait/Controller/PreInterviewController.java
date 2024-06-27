package aivle.ait.Controller;

import aivle.ait.Dto.PreInterviewDTO;
import aivle.ait.Security.Auth.CustomUserDetails;
import aivle.ait.Service.PreInterviewService;
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
@RequestMapping(value = "/interviewGroup/{interviewGroup_id}/preInterview", produces = MediaType.APPLICATION_JSON_VALUE)
public class PreInterviewController {
    private final PreInterviewService preInterviewService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    public ResponseEntity<PreInterviewDTO> create(@RequestBody PreInterviewDTO preInterviewDTO,
                                                  @PathVariable("interviewGroup_id") Long interviewGroup_id,
                                                  @AuthenticationPrincipal CustomUserDetails customUserDetails){
        PreInterviewDTO createdPreInterviewDTO = preInterviewService.create(customUserDetails.getCompany().getId(), interviewGroup_id, preInterviewDTO);

        if (createdPreInterviewDTO != null){
            return ResponseEntity.ok(createdPreInterviewDTO);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{preInterview_id}")
    public ResponseEntity<PreInterviewDTO> read(@PathVariable("interviewGroup_id") Long interviewGroup_id,
                                                @PathVariable("preInterview_id") Long preInterview_id,
                                                @AuthenticationPrincipal CustomUserDetails customUserDetails){
        PreInterviewDTO preInterviewDTO = preInterviewService.readOne(customUserDetails.getCompany().getId(), interviewGroup_id, preInterview_id);

        if (preInterviewDTO != null){
            return ResponseEntity.ok(preInterviewDTO);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/readAll")
    public ResponseEntity<List<PreInterviewDTO>> readAll(@PathVariable("interviewGroup_id") Long interviewGroup_id,
                                                         @AuthenticationPrincipal CustomUserDetails customUserDetails){
        List<PreInterviewDTO> preInterviewDTOS = preInterviewService.readAll(customUserDetails.getCompany().getId(), interviewGroup_id);

        if (preInterviewDTOS != null){
            return ResponseEntity.ok(preInterviewDTOS);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 내 면접 그룹들만 페이지 별로 불러오기 ex) localhost:8888/interviewGroup/{interviewGroup_id}/preInterview/read?page=0&size=5
    @GetMapping("/read")
    public ResponseEntity<Page<PreInterviewDTO>> readPage(@PageableDefault(size = 5) Pageable pageable,
                                                          @PathVariable("interviewGroup_id") Long interviewGroup_id,
                                                          @AuthenticationPrincipal CustomUserDetails customUserDetails){
        Page<PreInterviewDTO> preInterviewDTOS = preInterviewService.readAllPageable(customUserDetails.getCompany().getId(), interviewGroup_id, pageable);

        if (!preInterviewDTOS.isEmpty()){
            return ResponseEntity.ok(preInterviewDTOS);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{preInterview_id}/update")
    public ResponseEntity<PreInterviewDTO> update(@PathVariable("interviewGroup_id") Long interviewGroup_id,
                                                  @PathVariable("preInterview_id") Long preInterview_id,
                                                  @RequestBody PreInterviewDTO preInterviewDTO,
                                                  @AuthenticationPrincipal CustomUserDetails customUserDetails){
        PreInterviewDTO updatedPreInterviewDTO = preInterviewService.update(customUserDetails.getCompany().getId(), interviewGroup_id, preInterview_id, preInterviewDTO);

        if (updatedPreInterviewDTO != null){
            return ResponseEntity.ok(updatedPreInterviewDTO);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{preInterview_id}/delete")
    public ResponseEntity<PreInterviewDTO> delete(@PathVariable("interviewGroup_id") Long interviewGroup_id,
                                                  @PathVariable("preInterview_id") Long preInterview_id,
                                                  @AuthenticationPrincipal CustomUserDetails customUserDetails){
        PreInterviewDTO deletedPreInterviewDTO = preInterviewService.delete(customUserDetails.getCompany().getId(), interviewGroup_id, preInterview_id);

        if (deletedPreInterviewDTO != null){
            return ResponseEntity.ok(deletedPreInterviewDTO);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }
}
