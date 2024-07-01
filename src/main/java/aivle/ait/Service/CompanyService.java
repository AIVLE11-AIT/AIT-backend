package aivle.ait.Service;

import aivle.ait.Dto.CompanyDTO;
import aivle.ait.Entity.Company;
import aivle.ait.Repository.CompanyRepository;
import aivle.ait.Util.RedisUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    private final JavaMailSender javaMailSender;
    private final RedisUtil redisUtil;

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

    // ---- mail send ----
    // 인증 번호 생성
    public int createCode() {
        int randNum = (int)(Math.random() * 900000) + 100000; // 랜덤 6자리 숫자 생성
        return randNum;
    }

    // 인증번호 전송
    public String sendEmail(String to) {
        String checkNum = String.valueOf(createCode());
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("AIT: 회원가입 인증번호 안내");

            StringBuilder emailContent = new StringBuilder();
            emailContent.append("<html>");
            emailContent.append("<body>");
            emailContent.append("<h2>AIT 회원가입 인증번호 안내</h2>");
            emailContent.append("<p>안녕하세요. AIT 입니다.\n 회원가입을 위한 인증번호는 다음과 같습니다:</p>");
            emailContent.append("<h3>" + checkNum + "</h3>");
            emailContent.append("<p>인증번호를 입력하여 회원가입을 완료해 주세요.</p>");
            emailContent.append("<p>감사합니다.</p>");
            emailContent.append("</body>");
            emailContent.append("</html>");

            helper.setText(emailContent.toString(), true);
            javaMailSender.send(message);
            redisUtil.setDataExpire(checkNum, to, 60*5L); // 유효시간 5분 설정

        } catch(MessagingException e) {
            e.printStackTrace();
        }
        return checkNum;
    }

    // 인증번호 확인
    public boolean verifyCode(String email, String code) {
        String emailFounByCode = redisUtil.getData(code);

        if (emailFounByCode == null) {
            return false;
        }
        return emailFounByCode.equals(email);
    }
}
