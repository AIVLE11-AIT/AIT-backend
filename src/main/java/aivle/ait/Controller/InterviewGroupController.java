package aivle.ait.Controller;

import aivle.ait.Dto.InterviewGroupDTO;
import aivle.ait.Security.Auth.CustomUserDetails;
import aivle.ait.Service.InterviewGroupService;
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
@RequestMapping(value = "/interviewGroup", produces = MediaType.APPLICATION_JSON_VALUE)
public class InterviewGroupController {
    private final InterviewGroupService interviewGroupService;
    
    
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    public ResponseEntity<InterviewGroupDTO> create(@RequestBody InterviewGroupDTO interviewGroupDTO,
                                                    @AuthenticationPrincipal CustomUserDetails customUserDetails){
        InterviewGroupDTO createdBoard = interviewGroupService.create(customUserDetails.getCompany().getId(), interviewGroupDTO);

        if (createdBoard != null){
            return ResponseEntity.ok(createdBoard);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{interviewGroup_id}")
    public ResponseEntity<InterviewGroupDTO> read(@PathVariable Long interviewGroup_id,
                                                  @AuthenticationPrincipal CustomUserDetails customUserDetails){
        InterviewGroupDTO interviewGroupDTO = interviewGroupService.readOne(customUserDetails.getCompany().getId(), interviewGroup_id);

        if (interviewGroupDTO != null){
            return ResponseEntity.ok(interviewGroupDTO);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/readAll")
    public ResponseEntity<List<InterviewGroupDTO>> readAll(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        List<InterviewGroupDTO> interviewGroupDTOS = interviewGroupService.readAll(customUserDetails.getCompany().getId());

        if (!interviewGroupDTOS.isEmpty()){
            return ResponseEntity.ok(interviewGroupDTOS);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 내 면접 그룹들만 페이지 별로 불러오기 ex) localhost:8888/interviewGroup/read?page=0&size=5
    @GetMapping("/read")
    public ResponseEntity<Page<InterviewGroupDTO>> readPage(@PageableDefault(size = 5) Pageable pageable,
                                                            @AuthenticationPrincipal CustomUserDetails customUserDetails){
        Page<InterviewGroupDTO> interviewGroupDTOS = interviewGroupService.readAllPageable(customUserDetails.getCompany().getId(), pageable);

        if (!interviewGroupDTOS.isEmpty()){
            return ResponseEntity.ok(interviewGroupDTOS);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{interviewGroup_id}/update")
    public ResponseEntity<InterviewGroupDTO> update(@PathVariable Long interviewGroup_id, @RequestBody InterviewGroupDTO interviewGroupDTO,
                                                    @AuthenticationPrincipal CustomUserDetails customUserDetails){
        InterviewGroupDTO updatedInterviewGroup = interviewGroupService.update(customUserDetails.getCompany().getId(), interviewGroup_id, interviewGroupDTO);

        if (updatedInterviewGroup != null){
            return ResponseEntity.ok(updatedInterviewGroup);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{interviewGroup_id}/delete")
    public ResponseEntity<InterviewGroupDTO> delete(@PathVariable Long interviewGroup_id,
                                                    @AuthenticationPrincipal CustomUserDetails customUserDetails){
        InterviewGroupDTO deletedInterviewGroups = interviewGroupService.delete(customUserDetails.getCompany().getId(), interviewGroup_id);

        if (deletedInterviewGroups != null){
            return ResponseEntity.ok(deletedInterviewGroups);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }
}
