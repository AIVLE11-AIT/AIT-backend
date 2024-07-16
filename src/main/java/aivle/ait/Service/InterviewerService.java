package aivle.ait.Service;

import aivle.ait.Dto.CompanyDTO;
import aivle.ait.Dto.InterviewGroupDTO;
import aivle.ait.Dto.InterviewerDTO;
import aivle.ait.Entity.Company;
import aivle.ait.Entity.File;
import aivle.ait.Entity.InterviewGroup;
import aivle.ait.Entity.Interviewer;
import aivle.ait.Repository.InterviewGroupRepository;
import aivle.ait.Repository.InterviewerRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InterviewerService {
    private final InterviewGroupRepository interviewGroupRepository;
    private final InterviewerRepository interviewerRepository;
    private final JavaMailSender javaMailSender;
    private final FileService fileService;

    @Transactional
    public InterviewerDTO create(Long companyId, Long interviewGroupId, InterviewerDTO interviewerDTO){
        Optional<InterviewGroup> interviewGroup = interviewGroupRepository.findById(interviewGroupId);

        if (interviewGroup.isEmpty() || interviewGroup.get().getCompany().getId() != companyId) {
            return null;
        }

        Interviewer interviewer = new Interviewer();
        interviewer.setDtoToObject(interviewerDTO);
        interviewer.setInterviewgroup(interviewGroup.get());

        Interviewer createdInterviewer = interviewerRepository.save(interviewer);
        InterviewerDTO createdInterviewerDTO = new InterviewerDTO(createdInterviewer);

        return createdInterviewerDTO;
    }


    public InterviewerDTO readOne(Long companyId, Long interviewGroupId, Long interviewerId){
        Optional<Interviewer> interviewers = interviewerRepository.findInterviewerByIdAndInterviewgroupId(interviewerId, interviewGroupId);
        if (interviewers.isEmpty() || interviewers.get().getInterviewgroup().getCompany().getId() != companyId) {
            return null;
        }

        InterviewerDTO interviewerDTO = new InterviewerDTO(interviewers.get());
        return interviewerDTO;
    }

    public List<InterviewerDTO> readAll(Long companyId, Long interviewGroupId){
        Optional<InterviewGroup> interviewGroup = interviewGroupRepository.findById(interviewGroupId);
        if (interviewGroup.isEmpty() || interviewGroup.get().getCompany().getId() != companyId) {
            return null;
        }
        List<InterviewerDTO> interviewerDTOS = InterviewerDTO.convertToDto(interviewGroup.get().getInterviewers());
        return interviewerDTOS;
    }

    public Page<InterviewerDTO> readAllPageable(Long companyId, Long interviewGroupId, Pageable pageable){
        Optional<InterviewGroup> interviewGroup = interviewGroupRepository.findById(interviewGroupId);
        if (interviewGroup.isEmpty() || interviewGroup.get().getCompany().getId() != companyId) {
            return null;
        }

        Page<Interviewer> interviewers = interviewerRepository.findByInterviewgroupId(interviewGroupId, pageable);
        Page<InterviewerDTO> interviewerDTOS = InterviewerDTO.toDtoPage(interviewers);
        return interviewerDTOS;
    }

    public List<InterviewerDTO> readAllByIsPass(Boolean isPass, Long companyId, Long interviewGroupId){
        Optional<InterviewGroup> interviewGroup = interviewGroupRepository.findById(interviewGroupId);
        if (interviewGroup.isEmpty() || interviewGroup.get().getCompany().getId() != companyId) {
            return null;
        }

        List<Interviewer> interviewers = interviewerRepository.findByInterviewgroupIdAndResult_IsPass(interviewGroupId, isPass);
        if (interviewers.isEmpty()) {
            return new ArrayList<>();
        }

        return InterviewerDTO.convertToDto(interviewers);
    }

    @Transactional
    public InterviewerDTO update(Long companyId, Long interviewGroupId, Long preInterviewId, InterviewerDTO interviewerDTO){
        Optional<Interviewer> interviewers = interviewerRepository.findInterviewerByIdAndInterviewgroupId(preInterviewId, interviewGroupId);
        if (interviewers.isEmpty() || interviewers.get().getInterviewgroup().getCompany().getId() != companyId){
            return null;
        }

        Interviewer interviewer = interviewers.get();
        interviewer.setDtoToObject(interviewerDTO); // update

        InterviewerDTO createdInterviewerDTO = new InterviewerDTO(interviewer);
        return createdInterviewerDTO;
    }

    @Transactional
    public InterviewerDTO delete(Long companyId, Long interviewGroupId, Long preInterviewId){
        Optional<Interviewer> interviewers = interviewerRepository.findInterviewerByIdAndInterviewgroupId(preInterviewId, interviewGroupId);
        if (interviewers.isEmpty() || interviewers.get().getInterviewgroup().getCompany().getId() != companyId){
            return null;
        }

        Interviewer interviewer = interviewers.get();
        interviewerRepository.delete(interviewer);

        InterviewerDTO preInterviewDTO = new InterviewerDTO(interviewer);
        return preInterviewDTO;
    }

    // 링크 전송
    @Transactional
    @Async
    public void sendEmail(InterviewerDTO interviewerDTO, Long companyID, Long interviewGroup_id, String url) throws MessagingException {
        Optional<InterviewGroup> interviewGroupOptional = interviewGroupRepository.findInterviewGroupByIdAndCompanyId(interviewGroup_id, companyID);
        if (interviewGroupOptional.isEmpty() || interviewGroupOptional.get().getCompany().getId() != companyID) {
            System.out.println("그룹을 찾을 수 없음");
            throw new MessagingException("그룹을 찾을 수 없음 or companyId 불일치");
        }

        InterviewGroup interviewGroup = interviewGroupOptional.get();
        String email = interviewerDTO.getEmail();
        String mailTitle = interviewGroup.getName();

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(email);
        helper.setSubject("[" + mailTitle + "] AI 면접 안내");

        StringBuilder emailContent = makeEmailContent(interviewerDTO, interviewGroup, url);

        helper.setText(emailContent.toString(), true);
        javaMailSender.send(message);

        // InterviewGroup의 sendEmail 컬럼을 1로 업데이트
        InterviewGroupDTO interviewGroupDTO = new InterviewGroupDTO(interviewGroup);
        interviewGroupDTO.setSendEmail(1);
        interviewGroup.setDtoToObject(interviewGroupDTO); // update

    }

    // 메일 전송 폼 생성
    public StringBuilder makeEmailContent(InterviewerDTO interviewerDTO, InterviewGroup interviewGroup, String url) {
        String name = interviewerDTO.getName();
        String coName = interviewGroup.getCompany().getName();
        String interviewTitle = interviewGroup.getName();

        LocalDateTime start_date = interviewGroup.getStart_date();
        LocalDateTime end_date = interviewGroup.getEnd_date();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String startDate = start_date.format(formatter);
        String endDate = end_date.format(formatter);

        StringBuilder emailContent = new StringBuilder();
        emailContent.append("<!DOCTYPE html>");
        emailContent.append("<html>");
        emailContent.append("<head>");
        emailContent.append("</head>");
        emailContent.append("<body>");

        // ContentContainer 시작
        emailContent.append("<div style=\"width: 800px; height: 600px; border: 1px solid lightgray; margin: 100px auto; padding: 40px; box-sizing: border-box; border-radius: 10px; color: #000000; justify-content: center; align-items: center;\">");
        // Section 시작
        emailContent.append("<div style=\"color: #606060;");
        emailContent.append(" font-size: 15px;");
        emailContent.append(" font-weight: 500;");
        emailContent.append(" text-align: start;");
        emailContent.append(" width: 100%;\">");
        emailContent.append("<h2 style=\"margin-bottom: 30px; margin-top: 20px;");
        emailContent.append(" color: #303030;");
        emailContent.append(" font-size: 22px;");
        emailContent.append(" font-weight: 600;\">[");
        emailContent.append(interviewTitle + "] AI 면접 안내</h2>");
        emailContent.append("<p><strong>" + name + "</strong>님 안녕하세요.</p>");
        emailContent.append("<p>AI 면접은 <strong>" + startDate + " - " + endDate + "</strong> 기간 내에 진행 가능합니다.</p>");
        emailContent.append("<p style=\"margin-bottom: 30px;\">AI 면접은 " + "<a href=\"" + url + "\">" + url + "</a> 에서 온라인으로 진행됩니다.</p>");
        emailContent.append("</div>");
        // Section 끝

        // NoticeBox 시작
        emailContent.append("<div style=\"margin-top: 30px; margin-right: 30px;");
        emailContent.append(" width: 100%;");
        emailContent.append(" border-radius: 10px;");
        emailContent.append(" border: 2px solid #F1F1F2;");
        emailContent.append(" background: #FBFBFB;");
        emailContent.append(" color: #606060;");
        emailContent.append(" font-size: 15px;");
        emailContent.append(" font-weight: 500;");
        emailContent.append(" line-height: 1.2;\">");
        emailContent.append("<div style=\"margin-bottom: 20px; margin-top: 20px; margin-left: 20px;");
        emailContent.append(" color: #000;");
        emailContent.append(" font-size: 15px; text-align: start;");
        emailContent.append(" font-weight: 600;\">[주의사항]</div>");
        emailContent.append("<ul>");
        emailContent.append("<li>테스트 응시 전 이용 방법을 충분히 읽어본 후 면접에 응시하시기 바랍니다.</li>");
        emailContent.append("<li>면접 기간 내에 면접 진행을 완료해 주세요.</li>");
        emailContent.append("</ul>");
        emailContent.append("</div>");
        // NoticeBox 끝

        // GoToInterviewPageButton 시작
        emailContent.append("<div style=\"color: white; margin-top: 30px; margin-right: 30px;");
        emailContent.append(" font-size: 17px;");
        emailContent.append(" border-radius: 12px;");
        emailContent.append(" background: #696CEA;");
        emailContent.append(" width: 100%;");
        emailContent.append(" height: 45px;");
        emailContent.append(" display: flex;");
        emailContent.append(" justify-content: center;");
        emailContent.append(" align-items: center;");
        emailContent.append(" font-weight: 600;\">");
        emailContent.append("<a href=\"").append(url).append("\" style=\"color: white; padding: auto; text-decoration: none; text-align: center; width: 100%; margin-top: 10px;\">면접 페이지 바로가기</a>");
        emailContent.append("</div>");
        // GoToInterviewPageButton 끝


        // CenteredText 시작
        emailContent.append("<div style=\"color: #606060;");
        emailContent.append(" text-align: center;");
        emailContent.append(" font-size: 15px;");
        emailContent.append(" font-weight: 600;");
        emailContent.append(" line-height: 1.3;");
        emailContent.append(" margin-top: 30px;\">");
        emailContent.append("위의 내용을 반드시 숙지하시고 면접 응시에 착오 없으시길 바랍니다.<br />");
        emailContent.append("감사합니다.");
        emailContent.append("</div>");
        // CenteredText 끝

        // FooterText 시작
        emailContent.append("<div style=\"text-align: center;");
        emailContent.append(" color: #D9D9D9;");
        emailContent.append(" font-size: 15px;");
        emailContent.append(" font-weight: 400;");
        emailContent.append(" margin-top: 30px; margin-bottom: 20px;\">");
        emailContent.append("<p>2024 AIT · 서비스 이용약관 · 개인정보 처리방침</p>");
        emailContent.append("</div>");

        // ContentContainer 끝
        emailContent.append("</div>");

        // FooterText 끝
        emailContent.append("</body>");
        emailContent.append("</html>");




        return emailContent;
    }

    @Transactional
    public InterviewerDTO sendImagePath(Long interviewGroupId, Long interviewerId, String image_path) {
        Optional<Interviewer> interviewers = interviewerRepository.findInterviewerByIdAndInterviewgroupId(interviewerId, interviewGroupId);

        if (interviewers.isEmpty()) {
            return null;
        }

        Interviewer interviewer = interviewers.get();
        InterviewerDTO interviewerDTO = new InterviewerDTO(interviewer);
        interviewerDTO.setImage_path(image_path);
        interviewer.setDtoToObject(interviewerDTO);


        return interviewerDTO;
    }

    public Resource downloadImage(Long interviewer_id, Long companyId) {
        Optional<Interviewer> interviewerOptional = interviewerRepository.findById(interviewer_id);

        if (interviewerOptional.isEmpty() || interviewerOptional.get().getInterviewgroup().getCompany().getId() != companyId) {
            System.out.println("면접자 사진 없음 or companyID 불일치");
            return null;
        }

        Interviewer interviewer = interviewerOptional.get();
        String filePath = interviewer.getImage_path();

        Resource resource = fileService.read(filePath);
        if (resource == null) {
            System.out.println("면접자 사진 없음");
            return null;
        }
        return resource;
    }
}
