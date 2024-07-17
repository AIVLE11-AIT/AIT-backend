package aivle.ait.Controller;

import aivle.ait.Dto.CompanyDTO;
import aivle.ait.Entity.Company;
import aivle.ait.Repository.CompanyRepository;
import aivle.ait.Security.Auth.CustomUserDetails;
import aivle.ait.Service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class CompanyController {

    private final CompanyService companyService;
    private final CompanyRepository companyRepository;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup/register")
    public ResponseEntity<CompanyDTO> register(@RequestBody CompanyDTO companyDto){
        boolean check = companyService.getEmailCheck(companyDto.getEmail());

        if (!check){
            CompanyDTO createdCompanyDTO = companyService.register(companyDto);
            if (createdCompanyDTO != null){
                System.out.println("가입 성공");
                return ResponseEntity.ok(createdCompanyDTO);
            }
        }
        System.out.println("가입 실패");
        return ResponseEntity.badRequest().body(null);
    }

    @GetMapping("/check/register/{Email}")
    public ResponseEntity<Boolean> checkEmail(@PathVariable String Email) {
        Boolean check = companyService.getEmailCheck(Email);

        if (check){
            return ResponseEntity.ok(check);
        }
        else{
            return ResponseEntity.badRequest().body(check);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/check")
    public Boolean checkRole(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        Company company = customUserDetails.getCompany();
        if (company.getRole().equals("ROLE_ADMIN")){
            return true;
        }
        else{
            return false;
        }
    }

    // 인증번호 메일 전송
    @GetMapping("/signup/send/{email}")
    public ResponseEntity<String> sendEmail(@PathVariable String email) {
        try {
            companyService.sendEmail(email);
            return ResponseEntity.ok("Send mail success");
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error sending email: " + e.getMessage());
        }
    }

    // 인증번호 확인
    @PostMapping("/signup/verify/{email}")
    public ResponseEntity<String> verifyCode(@PathVariable String email, @RequestParam("code") String code) {
        try {
            if (companyService.verifyCode(email, code)) {
                return ResponseEntity.ok("Verifying success");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Verifying fail");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    // 수정
    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody CompanyDTO companyDTO, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        CompanyDTO updatedCompanyDTO = companyService.update(customUserDetails.getCompany().getId(), companyDTO);
        if (updatedCompanyDTO != null)
            return ResponseEntity.ok(updatedCompanyDTO);
        else
            return ResponseEntity.badRequest().body("update fail");
    }

    // 임시 비밀번호 발급
    @PostMapping("/sendTempPassword/{email}")
    public ResponseEntity<?> sendTempPassword(@PathVariable String email) {
        boolean isExist = companyRepository.existsByEmail(email);

        if (isExist) {
            String tempPassword = companyService.generateTemporalPassword();
            Company company = companyRepository.findByEmail(email);

            CompanyDTO companyDTO = new CompanyDTO();
            companyDTO.setId(company.getId());
            companyDTO.setName(company.getName());
            companyDTO.setPassword(tempPassword);
            companyDTO.setEmail(company.getEmail());

            CompanyDTO updatedCompanyDto = companyService.update(company.getId(), companyDTO);
            if (updatedCompanyDto != null) {
                companyService.sendEmailForTemporalPassword(email, tempPassword);
            }
            else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        } else {
            return ResponseEntity.badRequest().body("email not exist");
        }
        return ResponseEntity.ok("send temporal password successfully");
    }
}
