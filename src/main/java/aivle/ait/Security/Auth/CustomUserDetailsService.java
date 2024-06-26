package aivle.ait.Security.Auth;

import aivle.ait.Entity.Company;
import aivle.ait.Repository.CompanyRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final CompanyRepository companyRepository;

    public CustomUserDetailsService(CompanyRepository companyRepository) {

        this.companyRepository = companyRepository;

    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Company companyData = companyRepository.findByEmail(email);
        System.out.println("going");

        if (companyData != null) {
            System.out.println("memberdata");
            return new CustomUserDetails(companyData);

        }
        return null;
    }
}

