package aivle.ait.Service;

import aivle.ait.Dto.InterviewerDTO;
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

    // csv 파일 저장
    @Transactional
    public List<Interviewer> saveCsvData(MultipartFile file, Long companyId, Long interviewGroupId) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            List<Interviewer> interviewers = new ArrayList<>();
            String line;
            boolean isFirstLine = true;

            Optional<InterviewGroup> interviewGroup = interviewGroupRepository.findById(interviewGroupId);
            if (interviewGroup.isEmpty() || interviewGroup.get().getCompany().getId() != companyId) {
                return null;
            }

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] data = line.split(",");
                InterviewerDTO dto = InterviewerDTO.fromCsv(data);
                Interviewer interviewer = new Interviewer();
                interviewer.setDtoToObject(dto);
                interviewer.setInterviewgroup(interviewGroup.get());

                interviewers.add(interviewer);
            }
            return interviewerRepository.saveAll(interviewers);
        } catch (Exception e) {
            throw new RuntimeException("CSV 데이터 저장 중 오류 발생: " + e.getMessage());
        }
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
    public void sendEmail(InterviewerDTO interviewerDTO, String company, String url) throws MessagingException {
        String email = interviewerDTO.getEmail();
        String name = interviewerDTO.getName();

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(email);
        helper.setSubject("[" + company + "] AI 역량 검사 응시 안내");

        StringBuilder emailContent = new StringBuilder();
        emailContent.append("<html>");
        emailContent.append("<body>");
        emailContent.append("<h2>" + "[" + company + "] AI 역량 검사 응시 안내" + "</h2>");
        emailContent.append("<p>" + name + "님 안녕하세요.</p>");
        emailContent.append("<p>[" + company + "] AI 역량 검사 응시 링크를 안내해 드립니다.</p>");
        emailContent.append("<h3>" + url + "</h3>");
        emailContent.append("<p>감사합니다.</p>");
        emailContent.append("</body>");
        emailContent.append("</html>");

        helper.setText(emailContent.toString(), true);
        javaMailSender.send(message);
    }

}
