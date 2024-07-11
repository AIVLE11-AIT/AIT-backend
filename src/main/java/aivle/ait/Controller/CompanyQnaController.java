package aivle.ait.Controller;

import aivle.ait.Dto.CompanyQnaDTO;
import aivle.ait.Security.Auth.CustomUserDetails;
import aivle.ait.Service.CompanyQnaService;
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
@RequestMapping(value = "/interviewGroup/{interviewGroup_id}/companyQna", produces = MediaType.APPLICATION_JSON_VALUE)
public class CompanyQnaController {
    private final CompanyQnaService companyQnaService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    public ResponseEntity<CompanyQnaDTO> create(@RequestBody CompanyQnaDTO companyQnaDTO,
                                                @PathVariable("interviewGroup_id") Long interviewGroup_id,
                                                @AuthenticationPrincipal CustomUserDetails customUserDetails){
        CompanyQnaDTO createdCompanyQnaDTO = companyQnaService.create(customUserDetails.getCompany().getId(), interviewGroup_id, companyQnaDTO);

        if (createdCompanyQnaDTO != null){
            return ResponseEntity.ok(createdCompanyQnaDTO);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 면접 진행 시 로그인 없이도 질문을 가져와야 하므로 AuthenticationPrincipal 삭제
    @GetMapping("/{companyQna_id}")
    public ResponseEntity<CompanyQnaDTO> read(@PathVariable("interviewGroup_id") Long interviewGroup_id,
                                              @PathVariable("companyQna_id") Long companyQna_id){
        CompanyQnaDTO companyQnaDTO = companyQnaService.readOne(interviewGroup_id, companyQna_id);

        if (companyQnaDTO != null){
            return ResponseEntity.ok(companyQnaDTO);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 수정) 면접 진행 시 로그인 없이도 질문을 가져와야 하므로 AuthenticationPrincipal 삭제
    @GetMapping("/readAll")
    public ResponseEntity<List<CompanyQnaDTO>> readAll(@PathVariable("interviewGroup_id") Long interviewGroup_id){
        List<CompanyQnaDTO> companyQnaDTOS = companyQnaService.readAll(interviewGroup_id);

        if (companyQnaDTOS != null){
            return ResponseEntity.ok(companyQnaDTOS);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 내 면접 그룹들만 페이지 별로 불러오기 ex) localhost:8888/interviewGroup/{interviewGroup_id}/preInterview/read?page=0&size=5
    @GetMapping("/read")
    public ResponseEntity<Page<CompanyQnaDTO>> readPage(@PageableDefault(size = 5) Pageable pageable,
                                                        @PathVariable("interviewGroup_id") Long interviewGroup_id,
                                                        @AuthenticationPrincipal CustomUserDetails customUserDetails){
        Page<CompanyQnaDTO> companyQnaDTOS = companyQnaService.readAllPageable(customUserDetails.getCompany().getId(), interviewGroup_id, pageable);

        if (!companyQnaDTOS.isEmpty()){
            return ResponseEntity.ok(companyQnaDTOS);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{companyQna_id}/update")
    public ResponseEntity<CompanyQnaDTO> update(@PathVariable("interviewGroup_id") Long interviewGroup_id,
                                                @PathVariable("companyQna_id") Long companyQna_id,
                                                @RequestBody CompanyQnaDTO companyQnaDTO,
                                                @AuthenticationPrincipal CustomUserDetails customUserDetails){
        CompanyQnaDTO updatedCompanyQnaDTO = companyQnaService.update(customUserDetails.getCompany().getId(), interviewGroup_id, companyQna_id, companyQnaDTO);

        if (updatedCompanyQnaDTO != null){
            return ResponseEntity.ok(updatedCompanyQnaDTO);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{companyQna_id}/delete")
    public ResponseEntity<CompanyQnaDTO> delete(@PathVariable("interviewGroup_id") Long interviewGroup_id,
                                                @PathVariable("companyQna_id") Long companyQna_id,
                                                @AuthenticationPrincipal CustomUserDetails customUserDetails){
        CompanyQnaDTO deletedCompanyQnaDTO = companyQnaService.delete(customUserDetails.getCompany().getId(), interviewGroup_id, companyQna_id);

        if (deletedCompanyQnaDTO != null){
            return ResponseEntity.ok(deletedCompanyQnaDTO);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }
}
