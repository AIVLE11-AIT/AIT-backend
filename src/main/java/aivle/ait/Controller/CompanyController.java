package aivle.ait.Controller;

import aivle.ait.Dto.CompanyDTO;
import aivle.ait.Entity.Company;
import aivle.ait.Security.Auth.CustomUserDetails;
import aivle.ait.Service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
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

}
