package aivle.ait.Service;

import aivle.ait.Dto.CompanyQnaDTO;
import aivle.ait.Dto.InterviewGroupDTO;
import aivle.ait.Dto.InterviewerDTO;
import aivle.ait.Entity.Company;
import aivle.ait.Entity.CompanyQna;
import aivle.ait.Entity.InterviewGroup;
import aivle.ait.Entity.Interviewer;
import aivle.ait.Repository.CompanyRepository;
import aivle.ait.Repository.InterviewGroupRepository;
import aivle.ait.Repository.InterviewerRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InterviewGroupService {
    private final CompanyRepository companyRepository;
    private final InterviewGroupRepository interviewGroupRepository;
    private final InterviewerQnaService interviewerQnaService;


    @Transactional
    public InterviewGroupDTO create(Long companyId, InterviewGroupDTO interviewGroupDTO, MultipartFile file){
        Optional<Company> company = companyRepository.findById(companyId);

        if (company.isEmpty()) {
            return null;
        }

        InterviewGroup interviewGroup = new InterviewGroup();
        interviewGroup.setDtoToObject(interviewGroupDTO);
        interviewGroup.setCompany(company.get());

        // company_qna 연관 관계 설정
        for (CompanyQnaDTO companyQnaDTO : interviewGroupDTO.getCompanyQnas()){
            CompanyQna companyQna = new CompanyQna();
            companyQna.setDtoToObject(companyQnaDTO);
            companyQna.setInterviewgroup(interviewGroup);
        }

        List<InterviewerDTO> interviewerDTOS = getInterviewerDTOsFromCsv(file);
        interviewGroupDTO.setInterviewers(interviewerDTOS);

        // interviewer 연관 관계 설정
        for (InterviewerDTO interviewerDTO : interviewGroupDTO.getInterviewers()){
            Interviewer interviewer = new Interviewer();
            interviewer.setDtoToObject(interviewerDTO);
            interviewer.setInterviewgroup(interviewGroup);
        }

        InterviewGroup createdInterviewGroup = interviewGroupRepository.save(interviewGroup);
        InterviewGroupDTO createdInterviewGroupDTO = new InterviewGroupDTO(createdInterviewGroup);

        // 자소서 기반 질문 생성
        for (Interviewer interviewer : createdInterviewGroup.getInterviewers()){
            // async
            interviewerQnaService.create(interviewer);
        }

        return createdInterviewGroupDTO;
    }

    public List<InterviewerDTO> getInterviewerDTOsFromCsv(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            List<InterviewerDTO> interviewerDTOS = new ArrayList<>();
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());

            for(CSVRecord csvRecord: csvParser) {
                String name = csvRecord.get("name");
                String email = csvRecord.get("email");
                String birth = csvRecord.get("birth");
                String coverLetter = csvRecord.get("cover_letter");

                if (name.isEmpty() || email.isEmpty() || birth.isEmpty() || coverLetter.isEmpty()) {
                    throw new RuntimeException("CSV 데이터 저장 중 오류 발생: null error");
                }

                if (!isValidEmail(email)) {
                    throw new RuntimeException("CSV 데이터 저장 중 오류 발생: email 형식 error");
                }
                if (!isValidBirth(birth)) {
                    throw new RuntimeException("CSV 데이터 저장 중 오류 발생: birth 형식 error");
                }

                String[] data = {name, email, birth, coverLetter};
                InterviewerDTO dto = InterviewerDTO.fromCsv(data);
                interviewerDTOS.add(dto);
            }

            return interviewerDTOS;

        } catch (Exception e) {
            throw new RuntimeException("CSV 데이터 저장 중 오류 발생: " + e.getMessage());
        }
    }

    // 이메일 유효성 검사
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // 생년월일 유효성 검사
    public static boolean isValidBirth(String birth) {
        String birthDateRegex = "^\\d{4}-\\d{2}-\\d{2}$";
        Pattern pattern = Pattern.compile(birthDateRegex);
        Matcher matcher = pattern.matcher(birth);
        return matcher.matches();
    }

    public InterviewGroupDTO readOne(Long companyId, Long interviewGroupId){
        Optional<InterviewGroup> interviewGroup = interviewGroupRepository.findInterviewGroupByIdAndCompanyId(interviewGroupId, companyId);
        if (interviewGroup.isEmpty()) {
            return null;
        }

        InterviewGroupDTO interviewGroupDTO = new InterviewGroupDTO(interviewGroup.get());
        return interviewGroupDTO;
    }

    public List<InterviewGroupDTO> readAll(Long companyId){
        Optional<Company> company = companyRepository.findById(companyId);
        if (company.isEmpty()) {
            return null;
        }
        List<InterviewGroupDTO> interviewGroupDTOS = InterviewGroupDTO.convertToDto(company.get().getInterviewGroups());
        return interviewGroupDTOS;
    }

    public Page<InterviewGroupDTO> readAllPageable(Long companyId, Pageable pageable){
        Page<InterviewGroup> interviewGroups = interviewGroupRepository.findByCompanyId(companyId, pageable);
        Page<InterviewGroupDTO> interviewGroupDTOS = InterviewGroupDTO.toDtoPage(interviewGroups);
        return interviewGroupDTOS;
    }

    @Transactional
    public InterviewGroupDTO update(Long companyId, Long interviewGroupId, InterviewGroupDTO interviewGroupDTO){
        Optional<InterviewGroup> interviewGroups = interviewGroupRepository.findInterviewGroupByIdAndCompanyId(interviewGroupId, companyId);
        if (interviewGroups.isEmpty()){
            return null;
        }

        InterviewGroup interviewGroup = interviewGroups.get();
        interviewGroup.setDtoToObject(interviewGroupDTO); // update

        InterviewGroupDTO createdInterviewGroupDTO = new InterviewGroupDTO(interviewGroup);
        return createdInterviewGroupDTO;
    }

    @Transactional
    public InterviewGroupDTO delete(Long companyId, Long interviewGroupId){
        Optional<InterviewGroup> interviewGroups = interviewGroupRepository.findInterviewGroupByIdAndCompanyId(interviewGroupId, companyId);
        if (interviewGroups.isEmpty()){
            return null;
        }

        InterviewGroup interviewGroup = interviewGroups.get();
        interviewGroupRepository.delete(interviewGroup);

        InterviewGroupDTO interviewGroupDTO = new InterviewGroupDTO(interviewGroup);
        return interviewGroupDTO;
    }

    public List<InterviewGroupDTO> sortByCreatedAt(Long companyId){
        List<InterviewGroup> interviewGroups = interviewGroupRepository.findByCompanyIdOrderByCreatedDateDesc(companyId);
        if (interviewGroups.isEmpty()){
            return null;
        }

        return InterviewGroupDTO.convertToDto(interviewGroups);
    }
}
