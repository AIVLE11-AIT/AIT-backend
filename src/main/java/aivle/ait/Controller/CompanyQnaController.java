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
    public ResponseEntity<?> create(@RequestBody CompanyQnaDTO companyQnaDTO,
                                    @PathVariable("interviewGroup_id") Long interviewGroup_id,
                                    @AuthenticationPrincipal CustomUserDetails customUserDetails){
        try{
            CompanyQnaDTO createdCompanyQnaDTO = companyQnaService.create(customUserDetails.getCompany().getId(), interviewGroup_id, companyQnaDTO);

            if (createdCompanyQnaDTO != null){
                return ResponseEntity.ok(createdCompanyQnaDTO);
            }
            else{
                return ResponseEntity.badRequest().body("interviewGroup이 없거나 해당 계정의 소유가 아님.");
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

    }

    // 면접 진행 시 로그인 없이도 질문을 가져와야 하므로 AuthenticationPrincipal 삭제
    @GetMapping("/{companyQna_id}")
    public ResponseEntity<?> read(@PathVariable("interviewGroup_id") Long interviewGroup_id,
                                              @PathVariable("companyQna_id") Long companyQna_id){

        try{
            CompanyQnaDTO companyQnaDTO = companyQnaService.readOne(interviewGroup_id, companyQna_id);

            if (companyQnaDTO != null){
                return ResponseEntity.ok(companyQnaDTO);
            }
            else{
                return ResponseEntity.badRequest().body("interviewGroup_id, companyQna_id이 유효하지 않음.");
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    // 수정) 면접 진행 시 로그인 없이도 질문을 가져와야 하므로 AuthenticationPrincipal 삭제
    @GetMapping("/readAll")
    public ResponseEntity<?> readAll(@PathVariable("interviewGroup_id") Long interviewGroup_id){
        try{
            List<CompanyQnaDTO> companyQnaDTOS = companyQnaService.readAll(interviewGroup_id);

            if (companyQnaDTOS != null){
                return ResponseEntity.ok(companyQnaDTOS);
            }
            else{
                return ResponseEntity.badRequest().body("interviewGroup_id이 유효하지 않음.");
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    // 내 면접 그룹들만 페이지 별로 불러오기 ex) localhost:8888/interviewGroup/{interviewGroup_id}/preInterview/read?page=0&size=5
    @GetMapping("/read")
    public ResponseEntity<?> readPage(@PageableDefault(size = 5) Pageable pageable,
                                                        @PathVariable("interviewGroup_id") Long interviewGroup_id,
                                                        @AuthenticationPrincipal CustomUserDetails customUserDetails){
        try{
            Page<CompanyQnaDTO> companyQnaDTOS = companyQnaService.readAllPageable(customUserDetails.getCompany().getId(), interviewGroup_id, pageable);

            if (!companyQnaDTOS.isEmpty()){
                return ResponseEntity.ok(companyQnaDTOS);
            }
            else{
                return ResponseEntity.badRequest().body("interviewGroup이 없거나 해당 계정의 소유가 아님.");
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/{companyQna_id}/update")
    public ResponseEntity<?> update(@PathVariable("interviewGroup_id") Long interviewGroup_id,
                                                @PathVariable("companyQna_id") Long companyQna_id,
                                                @RequestBody CompanyQnaDTO companyQnaDTO,
                                                @AuthenticationPrincipal CustomUserDetails customUserDetails){
        try{
            CompanyQnaDTO updatedCompanyQnaDTO = companyQnaService.update(customUserDetails.getCompany().getId(), interviewGroup_id, companyQna_id, companyQnaDTO);

            if (updatedCompanyQnaDTO != null){
                return ResponseEntity.ok(updatedCompanyQnaDTO);
            }
            else{
                return ResponseEntity.badRequest().body("companyQna가 없거나 해당 계정의 소유가 아님.");
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

    }

    @DeleteMapping("/{companyQna_id}/delete")
    public ResponseEntity<?> delete(@PathVariable("interviewGroup_id") Long interviewGroup_id,
                                    @PathVariable("companyQna_id") Long companyQna_id,
                                    @AuthenticationPrincipal CustomUserDetails customUserDetails){
        try{
            CompanyQnaDTO deletedCompanyQnaDTO = companyQnaService.delete(customUserDetails.getCompany().getId(), interviewGroup_id, companyQna_id);

            if (deletedCompanyQnaDTO != null){
                return ResponseEntity.ok(deletedCompanyQnaDTO);
            }
            else{
                return ResponseEntity.badRequest().body("companyQna가 없거나 해당 계정의 소유가 아님.");
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

    }
}
