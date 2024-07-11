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
    public boolean sendEmail(InterviewerDTO interviewerDTO, Long companyID, Long interviewGroup_id, String url) throws MessagingException {
        Optional<InterviewGroup> interviewGroupOptional = interviewGroupRepository.findInterviewGroupByIdAndCompanyId(interviewGroup_id, companyID);
        if (interviewGroupOptional.isEmpty() || interviewGroupOptional.get().getCompany().getId() != companyID) {
            System.out.println("그룹을 찾을 수 없음");
            return false;
        }

        InterviewGroup interviewGroup = interviewGroupOptional.get();
        String email = interviewerDTO.getEmail();
        String mailTitle = interviewGroup.getName();

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(email);
        helper.setSubject("[" + mailTitle + "] AI 역량검사 응시 안내");

        StringBuilder emailContent = makeEmailContent(interviewerDTO, interviewGroup);

        helper.setText(emailContent.toString(), true);
        javaMailSender.send(message);

        // InterviewGroup의 sendEmail 컬럼을 1로 업데이트
        InterviewGroupDTO interviewGroupDTO = new InterviewGroupDTO(interviewGroup);
        interviewGroupDTO.setSendEmail(1);
        interviewGroup.setDtoToObject(interviewGroupDTO); // update

        return true;
    }

    // 메일 전송 폼 생성
    public StringBuilder makeEmailContent(InterviewerDTO interviewerDTO, InterviewGroup interviewGroup) {
        String name = interviewerDTO.getName();
        String coName = interviewGroup.getCompany().getName();

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
        emailContent.append("<div>");
        emailContent.append("test " + name + "님 " + coName + " 역량검사 " + startDate + " ~ " + endDate);
        emailContent.append("</div>");
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
