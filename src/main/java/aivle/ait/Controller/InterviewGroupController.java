package aivle.ait.Controller;

import aivle.ait.Dto.InterviewGroupDTO;
import aivle.ait.Dto.InterviewerDTO;
import aivle.ait.Security.Auth.CustomUserDetails;
import aivle.ait.Service.InterviewGroupService;
import aivle.ait.Service.InterviewerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/interviewGroup", produces = MediaType.APPLICATION_JSON_VALUE)
public class InterviewGroupController {
    private final InterviewGroupService interviewGroupService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestPart(value="InterviewGroupDTO") InterviewGroupDTO interviewGroupDTO,
                                                    @RequestPart(value="InterviewerDTO") List<InterviewerDTO> interviewerDTOs,
                                                    @AuthenticationPrincipal CustomUserDetails customUserDetails){
        try{
            InterviewGroupDTO createdInterviewGroupDTO = interviewGroupService.create(customUserDetails.getCompany().getId(), interviewGroupDTO, interviewerDTOs);

            if (createdInterviewGroupDTO != null){
                return ResponseEntity.ok(createdInterviewGroupDTO);
            }
            else{
                return ResponseEntity.badRequest().body("로그인, 입력데이터 확인 필요.");
            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

    }
    @GetMapping("/{interviewGroup_id}")
    public ResponseEntity<?> read(@PathVariable Long interviewGroup_id,
                                                  @AuthenticationPrincipal CustomUserDetails customUserDetails){
        try{
            InterviewGroupDTO interviewGroupDTO = interviewGroupService.readOne(customUserDetails.getCompany().getId(), interviewGroup_id);

            if (interviewGroupDTO != null){
                return ResponseEntity.ok(interviewGroupDTO);
            }
            else{
                return ResponseEntity.badRequest().body("interviewGroup이 없음.");
            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

    }

    @GetMapping("/readAll")
    public ResponseEntity<?> readAll(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        try{
            List<InterviewGroupDTO> interviewGroupDTOS = interviewGroupService.readAll(customUserDetails.getCompany().getId());

            if (interviewGroupDTOS != null){
                return ResponseEntity.ok(interviewGroupDTOS);
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

    // 내 면접 그룹들만 페이지 별로 불러오기 ex) localhost:8888/interviewGroup/read?page=0&size=5
    @GetMapping("/read")
    public ResponseEntity<?> readPage(@PageableDefault(size = 5) Pageable pageable,
                                                            @AuthenticationPrincipal CustomUserDetails customUserDetails){
        Page<InterviewGroupDTO> interviewGroupDTOS = interviewGroupService.readAllPageable(customUserDetails.getCompany().getId(), pageable);

        if (!interviewGroupDTOS.isEmpty()){
            return ResponseEntity.ok(interviewGroupDTOS);
        }
        else{
            return ResponseEntity.ok().body("{}");
        }
    }

    @PutMapping("/{interviewGroup_id}/update")
    public ResponseEntity<InterviewGroupDTO> update(@PathVariable Long interviewGroup_id, @RequestBody InterviewGroupDTO interviewGroupDTO,
                                                    @AuthenticationPrincipal CustomUserDetails customUserDetails){
        InterviewGroupDTO updatedInterviewGroup = interviewGroupService.update(customUserDetails.getCompany().getId(), interviewGroup_id, interviewGroupDTO);

        if (updatedInterviewGroup != null){
            return ResponseEntity.ok(updatedInterviewGroup);
        }
        else return ResponseEntity.badRequest().body(null);
    }

    @DeleteMapping("/{interviewGroup_id}/delete")
    public ResponseEntity<?> delete(@PathVariable Long interviewGroup_id,
                                    @AuthenticationPrincipal CustomUserDetails customUserDetails){
        try{
            InterviewGroupDTO deletedInterviewGroups = interviewGroupService.delete(customUserDetails.getCompany().getId(), interviewGroup_id);

            if (deletedInterviewGroups != null){
                return ResponseEntity.ok(deletedInterviewGroups);
            }
            else{
                return ResponseEntity.badRequest().body("interviewGroup이 없음.");
            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

    }

    @GetMapping("/check")
    public ResponseEntity<?> check(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        List<InterviewGroupDTO> interviewGroupDTOS = interviewGroupService.sortByCreatedAt(customUserDetails.getCompany().getId());

        if (interviewGroupDTOS == null || interviewGroupDTOS.isEmpty()){
            return ResponseEntity.badRequest().body("NOT EXISTS");
        }

        return ResponseEntity.ok(interviewGroupDTOS.get(0).getCompanyQnas());
    }

    // 메일 전송 여부 체크
    @GetMapping("/{interviewGroup_id}/checkEmail")
    public ResponseEntity<?> checkEmail(@PathVariable Long interviewGroup_id,
                                        @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Boolean sendEmail = interviewGroupService.checkEmail(customUserDetails.getCompany().getId(), interviewGroup_id);
        if (sendEmail == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("send email fail!");
        }
        else return ResponseEntity.ok(sendEmail);
    }
}
