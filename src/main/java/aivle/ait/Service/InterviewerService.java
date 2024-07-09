package aivle.ait.Service;

import aivle.ait.Dto.CompanyDTO;
import aivle.ait.Dto.InterviewGroupDTO;
import aivle.ait.Dto.InterviewerDTO;
import aivle.ait.Entity.Company;
import aivle.ait.Entity.InterviewGroup;
import aivle.ait.Entity.Interviewer;
import aivle.ait.Repository.InterviewGroupRepository;
import aivle.ait.Repository.InterviewerRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
    public boolean sendEmail(InterviewerDTO interviewerDTO, Long companyID, Long interviewGroup_id, String url) throws MessagingException {
        Optional<InterviewGroup> interviewGroupOptional = interviewGroupRepository.findInterviewGroupByIdAndCompanyId(interviewGroup_id, companyID);
        if (interviewGroupOptional.isEmpty() || interviewGroupOptional.get().getId() != companyID) {
            System.out.println("그룹을 찾을 수 없음");
            return false;
        }

        String email = interviewerDTO.getEmail();
        String name = interviewerDTO.getName();

        InterviewGroup interviewGroup = interviewGroupOptional.get();
        String mailTitle = interviewGroup.getName();
        String coName = interviewGroup.getCompany().getName();

        LocalDateTime start_date = interviewGroup.getStart_date();
        LocalDateTime end_date = interviewGroup.getEnd_date();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String startDate = start_date.format(formatter);
        String endDate = end_date.format(formatter);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(email);
        helper.setSubject("[" + mailTitle + "] AI 역량검사 응시 안내");

        StringBuilder emailContent = new StringBuilder();
        emailContent.append("<!DOCTYPE html>");
        emailContent.append("<html>");
        emailContent.append("<head>");
        emailContent.append("</head>");
        emailContent.append("<body>");
        emailContent.append("");
        emailContent.append("</body>");
        emailContent.append("</html>");

//        emailContent.append(
//                "<div style=\"width: 500px; height: 600px; border-top: 3px solid #696CEA; margin: 100px auto; padding: 30px 0; box-sizing: border-box; color: #000000;\">"
//                        + "    <h1 style=\"margin: 0; padding: 0 5px; font-size: 25px; font-weight: 600;\">"
//                        + "        <span style=\"font-size: 20px; color: #000000;\">AIT</span><br />"
//                        + "        <span style=\"color: #696CEA\">[" + mailTitle + "] AI 역량검사 응시 안내 </span> 입니다."
//                        + "    </h1>\n"
//                        + "    <p style=\"font-size: 16px; line-height: 26px; margin-top: 50px; padding: 0 3px; color: #000000;\">"
//                        + "안녕하세요 " + name + "님, " + coName + " 입니다. <br /> <br />"
//                        + "AI 역량검사가 다음과 같이 진행됩니다. <br/><br/> 아래 내용을 반드시 숙지하시고, AI 역량검사 응시에 착오 없으시길 바랍니다.</p>"
//                        + "    <div style=\"width: 500px; font-size: 18px; margin-top: 30px; padding: 10px 0; background-color: #696CEA; border: 1px solid #696CEA; display: inline-block; color: #000000; text-align: center;\">"
//                        + "        <h3 style=\"margin: 0; padding: 10px; color: #ffffff;\">" + password + "</h3>"
//                        + "    </div>"
//                        + "    <p style=\"font-size: 16px; line-height: 26px; margin-top: 30px; padding: 0 3px; color: #000000;\">"
//                        + "로그인 후 비밀번호를 반드시 변경해 주세요!<br/><br/>감사합니다.</p>"
//                        + "</div>");

        helper.setText(emailContent.toString(), true);
        javaMailSender.send(message);

        return true;
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
}
