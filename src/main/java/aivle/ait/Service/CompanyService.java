package aivle.ait.Service;

import aivle.ait.Dto.CompanyDTO;
import aivle.ait.Entity.Company;
import aivle.ait.Repository.CompanyRepository;
import aivle.ait.Util.RedisUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
            helper.setSubject("[AIT] 회원가입 인증번호 안내");


            StringBuilder emailContent = new StringBuilder();
            emailContent.append("<!DOCTYPE html>");
            emailContent.append("<html>");
            emailContent.append("<head>");
            emailContent.append("</head>");
            emailContent.append("<body>");
            emailContent.append(
                    "<div style=\"width: 500px; height: 600px; border-top: 3px solid #696CEA; margin: 100px auto; padding: 30px 0; box-sizing: border-box; color: #000000;\">"
                            + "    <h1 style=\"margin: 0; padding: 0 5px; font-size: 25px; font-weight: 600;\">"
                            + "        <span style=\"font-size: 20px; color: #000000;\">AIT</span><br />"
                            + "        <span style=\"color: #696CEA\">회원가입 인증번호 안내</span> 입니다."
                            + "    </h1>\n"
                            + "    <p style=\"font-size: 16px; line-height: 26px; margin-top: 50px; padding: 0 3px; color: #000000;\">"
                            + "안녕하세요.<br/><br/>AIT에 가입해 주셔서 진심으로 감사드립니다.<br /> <br />"
                            + "회원 가입을 위한 인증번호를 안내해 드립니다.</p>"
                            + "    <div style=\"width: 500px; font-size: 18px; margin-top: 30px; padding: 10px 0; background-color: #696CEA; border: 1px solid #696CEA; display: inline-block; color: #000000; text-align: center;\">"
                            + "        <h3 style=\"margin: 0; padding: 10px; color: #ffffff;\">" + checkNum + "</h3>"
                            + "    </div>"
                            + "    <p style=\"font-size: 16px; line-height: 26px; margin-top: 30px; padding: 0 3px; color: #000000;\">"
                            + "인증번호를 입력하여 회원가입을 완료해 주세요.<br/><br/>감사합니다.</p>"
                            + "</div>");
            emailContent.append("</body>");
            emailContent.append("</html>");

            helper.setText(emailContent.toString(), true);
            javaMailSender.send(message);

            if (redisUtil.getData(to) != null) {
                System.out.println("codeFoundByEmail: " + redisUtil.getData(to));
                redisUtil.deleteData(to);
            }
            redisUtil.setDataExpire(to, checkNum, 60 * 5L); // 유효시간 5분 설정

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return checkNum;
    }

    // 인증번호 확인
    public boolean verifyCode(String email, String code) {
        String codeFoundByEmail = redisUtil.getData(email);

        if (codeFoundByEmail == null) {
            return false;
        }
        return codeFoundByEmail.equals(code);
    }

    // update
    @Transactional
    public CompanyDTO update(Long companyId, CompanyDTO companyDTO) {
        Optional<Company> companyOptional = companyRepository.findById(companyId);

        if (companyOptional.isEmpty()) return null;

        companyDTO.setRole("ROLE_USER");
        String encodedPassword = passwordEncoder.encode(companyDTO.getPassword());
        companyDTO.setPassword(encodedPassword);

        Company company = companyOptional.get();
        company.setDtoToObject(companyDTO); // update

        CompanyDTO updatedCompanyDTO = new CompanyDTO(company);
        return updatedCompanyDTO;
    }

    // 임시 비번 생성
    public String generateTemporalPassword() {
        char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
        String str = "";

        // 랜덤으로 10개를 chatSet에서 뽑아서 임시 비번 생성
        int idx = 0;
        for (int i=0;i<10;i++) {
            idx = (int) (charSet.length * Math.random());
            str += charSet[idx];
        }
        return str;
    }

    public String sendEmailForTemporalPassword(String to, String password) {
        try {
            // 메일 전송
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("[AIT] 임시 비밀번호 안내");

            StringBuilder emailContent = new StringBuilder();
            emailContent.append("<!DOCTYPE html>");
            emailContent.append("<html>");
            emailContent.append("<head>");
            emailContent.append("</head>");
            emailContent.append("<body>");
            emailContent.append(
                    "<div style=\"width: 500px; height: 600px; border-top: 3px solid #696CEA; margin: 100px auto; padding: 30px 0; box-sizing: border-box; color: #000000;\">"
                            + "    <h1 style=\"margin: 0; padding: 0 5px; font-size: 25px; font-weight: 600;\">"
                            + "        <span style=\"font-size: 20px; color: #000000;\">AIT</span><br />"
                            + "        <span style=\"color: #696CEA\">임시 비밀번호 안내</span> 입니다."
                            + "    </h1>\n"
                            + "    <p style=\"font-size: 16px; line-height: 26px; margin-top: 50px; padding: 0 3px; color: #000000;\">"
                            + "안녕하세요 AIT입니다.<br /> <br />"
                            + "임시 비밀번호를 안내해 드립니다.</p>"
                            + "    <div style=\"width: 500px; font-size: 18px; margin-top: 30px; padding: 10px 0; background-color: #696CEA; border: 1px solid #696CEA; display: inline-block; color: #000000; text-align: center;\">"
                            + "        <h3 style=\"margin: 0; padding: 10px; color: #ffffff;\">" + password + "</h3>"
                            + "    </div>"
                            + "    <p style=\"font-size: 16px; line-height: 26px; margin-top: 30px; padding: 0 3px; color: #000000;\">"
                            + "로그인 후 비밀번호를 반드시 변경해 주세요!<br/><br/>감사합니다.</p>"
                            + "</div>");
            emailContent.append("</body>");
            emailContent.append("</html>");

            helper.setText(emailContent.toString(), true);
            javaMailSender.send(message);


        } catch(MessagingException e) {
            e.printStackTrace();
        }
        return password;
    }
}
