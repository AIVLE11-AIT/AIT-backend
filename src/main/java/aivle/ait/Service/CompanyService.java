package aivle.ait.Service;

import aivle.ait.Dto.CompanyDTO;
import aivle.ait.Entity.Company;
import aivle.ait.Repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public CompanyDTO register(CompanyDTO companyDto){
        String encodedPassword = passwordEncoder.encode(companyDto.getPassword());
        Company company = new Company();
        company.setName(companyDto.getName());
        company.setPassword(encodedPassword);
        company.setEmail(companyDto.getEmail());
        company.setRole("ROLE_USER");

        companyRepository.save(company);

        CompanyDTO createdCompanyDTO = new CompanyDTO(company);
        return createdCompanyDTO;
    }

    public boolean getEmailCheck(String email){
        boolean check = companyRepository.existsByEmail(email);
        return check;
    }

    public CompanyDTO getMemberDto(String email){
        Company company = companyRepository.findByEmail(email);

        CompanyDTO companyDto = new CompanyDTO(company);
        return companyDto;
    }

}
