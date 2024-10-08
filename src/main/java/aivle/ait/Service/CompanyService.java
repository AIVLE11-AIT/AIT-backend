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
        company.setRole("ROLE_COMMON");

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

            StringBuilder emailContent = makeEmailContentForJoin(checkNum);

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

    public StringBuilder makeEmailContentForJoin(String checkNum) {
        StringBuilder emailContent = new StringBuilder();
        emailContent.append("<!DOCTYPE html>");
        emailContent.append("<html>");
        emailContent.append("<head>");
        emailContent.append("</head>");
        emailContent.append("<body>");

        // ContentContainer 시작
        emailContent.append("<div style=\"width: 800px; height: 450px; border: 1px solid lightgray; margin: 100px auto; padding: 30px; box-sizing: border-box; color: #000000; justify-content: center; align-items: center;\">");

        // Section 시작
        emailContent.append("<div style=\"color: #606060;");
        emailContent.append(" font-size: 15px;");
        emailContent.append(" font-weight: 500;");
        emailContent.append(" text-align: center;");
        emailContent.append(" width: 100%;\">");
        emailContent.append("<h2 style=\"margin-bottom: 30px; margin-top: 20px;");
        emailContent.append(" color: #696CEA;");
        emailContent.append(" font-size: 22px;");
        emailContent.append(" font-weight: 600;\">[AIT] 회원가입 인증번호 안내</h2>");
        emailContent.append("<p>안녕하세요, AIT 입니다.</p>");
        emailContent.append("<p>AIT에 가입해 주셔서 진심으로 감사드립니다.</p>");
        emailContent.append("<p>회원가입을 위한 인증번호를 안내해 드립니다.</p>");
        emailContent.append("</div>");
        // Section 끝

        // NoticeBox 시작
        emailContent.append("<div style=\"margin-top: 30px; margin-right: 30px;");
        emailContent.append(" width: 100%;");
        emailContent.append(" border-radius: 10px;");
        emailContent.append(" border: 3px solid #F2F2F2;");
        emailContent.append(" background: #F2F2F2;");
        emailContent.append(" font-size: 15px;");
        emailContent.append(" font-weight: 500;");
        emailContent.append(" line-height: 1.2; text-align: center;\">");
        emailContent.append("<div style=\"margin-bottom: 15px; margin-top: 20px;");
        emailContent.append(" font-size: 15px; text-align: center;");
        emailContent.append(" font-weight: 600;\">[인증번호]</div>");
        emailContent.append("<p style=\"font-size: 18px; margin-bottom: 15px; color: #696CEA;\"><strong>" + checkNum + "</strong></p>");
        emailContent.append("<div style=\"font-size: 13px; color: #969696; margin-bottom: 15px;\"><p>*개인정보 보호를 위해 인증번호는 5분간 유효합니다.</p></div>");
        emailContent.append("</div>");
        // NoticeBox 끝

        // CenteredText 시작
        emailContent.append("<div style=\"color: #606060;");
        emailContent.append(" text-align: center;");
        emailContent.append(" font-size: 15px;");
        emailContent.append(" font-weight: 600;");
        emailContent.append(" line-height: 1.3;");
        emailContent.append(" margin-top: 30px;\">");
        emailContent.append("인증번호를 입력하여 회원가입을 완료해 주세요.<br />");
        emailContent.append("감사합니다.");
        emailContent.append("</div>");
        // CenteredText 끝

        // ContentContainer 끝
        emailContent.append("</div>");

        // FooterText 끝
        emailContent.append("</body>");
        emailContent.append("</html>");


        return emailContent;
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

            StringBuilder emailContent = makeEmailContentForPassword(password);

            helper.setText(emailContent.toString(), true);
            javaMailSender.send(message);


        } catch(MessagingException e) {
            e.printStackTrace();
        }
        return password;
    }

    public StringBuilder makeEmailContentForPassword(String password) {
        StringBuilder emailContent = new StringBuilder();
        emailContent.append("<!DOCTYPE html>");
        emailContent.append("<html>");
        emailContent.append("<head>");
        emailContent.append("</head>");
        emailContent.append("<body>");

        // ContentContainer 시작
        emailContent.append("<div style=\"width: 800px; height: 400px; border: 1px solid lightgray; margin: 100px auto; padding: 30px; box-sizing: border-box; color: #000000; justify-content: center; align-items: center;\">");

        // Section 시작
        emailContent.append("<div style=\"color: #606060;");
        emailContent.append(" font-size: 15px;");
        emailContent.append(" font-weight: 500;");
        emailContent.append(" text-align: center;");
        emailContent.append(" width: 100%;\">");
        emailContent.append("<h2 style=\"margin-bottom: 30px; margin-top: 20px;");
        emailContent.append(" color: #696CEA;");
        emailContent.append(" font-size: 22px;");
        emailContent.append(" font-weight: 600;\">[AIT] 임시 비밀번호 안내</h2>");
        emailContent.append("<p>안녕하세요, AIT 입니다.</p>");
        emailContent.append("<p>임시 비밀번호를 안내해 드립니다.</p>");
        emailContent.append("</div>");
        // Section 끝

        // NoticeBox 시작
        emailContent.append("<div style=\"margin-top: 30px; margin-right: 30px;");
        emailContent.append(" width: 100%;");
        emailContent.append(" border-radius: 10px;");
        emailContent.append(" border: 3px solid #F2F2F2;");
        emailContent.append(" background: #F2F2F2;");
        emailContent.append(" font-size: 15px;");
        emailContent.append(" font-weight: 500;");
        emailContent.append(" line-height: 1.2; text-align: center;\">");
        emailContent.append("<div style=\"margin-bottom: 25px; margin-top: 20px;");
        emailContent.append(" font-size: 15px; text-align: center;");
        emailContent.append(" font-weight: 600;\">[임시 비밀번호]</div>");
        emailContent.append("<p style=\"font-size: 18px; margin-bottom: 15px; color: #696CEA;\"><strong>" + password + "</strong></p>");
        emailContent.append("</div>");
        // NoticeBox 끝


        // CenteredText 시작
        emailContent.append("<div style=\"color: #606060;");
        emailContent.append(" text-align: center;");
        emailContent.append(" font-size: 15px;");
        emailContent.append(" font-weight: 600;");
        emailContent.append(" line-height: 1.3;");
        emailContent.append(" margin-top: 30px;\">");
        emailContent.append("로그인 후 반드시 비밀번호를 변경해 주세요!<br />");
        emailContent.append("감사합니다.");
        emailContent.append("</div>");
        // CenteredText 끝

        // ContentContainer 끝
        emailContent.append("</div>");

        // FooterText 끝
        emailContent.append("</body>");
        emailContent.append("</html>");


        return emailContent;

    }
}
