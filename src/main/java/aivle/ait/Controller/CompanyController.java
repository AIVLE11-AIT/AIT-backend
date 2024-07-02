package aivle.ait.Controller;

import aivle.ait.Dto.CompanyDTO;
import aivle.ait.Entity.Company;
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
@RequestMapping(value = "/signup", produces = MediaType.APPLICATION_JSON_VALUE)
public class CompanyController {

    private final CompanyService companyService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
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
    public String checkEmail(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        Company company = customUserDetails.getCompany();

        return company.getEmail();
    }

    // 인증번호 메일 전송
    @GetMapping("/send/{email}")
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
    @PostMapping("/verify/{email}")
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

}
