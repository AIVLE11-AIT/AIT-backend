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
import java.time.LocalDateTime;
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
    public InterviewGroupDTO create(Long companyId, InterviewGroupDTO interviewGroupDTO, List<InterviewerDTO> interviewerDTOs){
        Optional<Company> company = companyRepository.findById(companyId);

        if (company.isEmpty() || interviewGroupDTO == null || interviewerDTOs.isEmpty() || interviewerDTOs.isEmpty()) {
            return null;
        }

        interviewGroupDTO.setInterviewers(interviewerDTOs);

        InterviewGroup interviewGroup = new InterviewGroup();
        interviewGroup.setDtoToObject(interviewGroupDTO);
        interviewGroup.setCompany(company.get());

        // company_qna 연관 관계 설정
        for (CompanyQnaDTO companyQnaDTO : interviewGroupDTO.getCompanyQnas()){
            CompanyQna companyQna = new CompanyQna();
            companyQna.setDtoToObject(companyQnaDTO);
            companyQna.setInterviewgroup(interviewGroup);
        }

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

    // 메일 전송 확인
    public boolean checkEmail(Long companyId, Long interviewGroupId) {
        Optional<InterviewGroup> interviewGroupOptional = interviewGroupRepository.findInterviewGroupByIdAndCompanyId(interviewGroupId, companyId);
        if (interviewGroupOptional.isEmpty() || interviewGroupOptional.get().getCompany().getId() != companyId) {
            System.out.println("면접 그룹 없음 or companyId 불일치");
            return false;
        }

        InterviewGroup interviewGroup = interviewGroupOptional.get();
        if (interviewGroup.getSendEmail() == 0) return false;
        return true;
    }
}
