package aivle.ait.Service;

import aivle.ait.Dto.CompanyDTO;
import aivle.ait.Dto.CompanyQnaDTO;
import aivle.ait.Entity.InterviewGroup;
import aivle.ait.Entity.CompanyQna;
import aivle.ait.Repository.InterviewGroupRepository;
import aivle.ait.Repository.CompanyQnaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyQnaService {
    private final InterviewGroupRepository interviewGroupRepository;
    private final CompanyQnaRepository companyQnaRepository;

    @Transactional
    public CompanyQnaDTO create(Long companyId, Long interviewGroupId, CompanyQnaDTO companyQnaDTO){
        Optional<InterviewGroup> interviewGroup = interviewGroupRepository.findById(interviewGroupId);

        if (interviewGroup.isEmpty() || interviewGroup.get().getCompany().getId() != companyId) {
            return null;
        }

        CompanyQna companyqna = new CompanyQna();
        companyqna.setDtoToObject(companyQnaDTO);
        companyqna.setInterviewgroup(interviewGroup.get());

        CompanyQna createdCompanyqna = companyQnaRepository.save(companyqna);
        CompanyQnaDTO createdCompanyQnaDTO = new CompanyQnaDTO(createdCompanyqna);

        return createdCompanyQnaDTO;
    }

    public CompanyQnaDTO readOne(Long interviewGroupId, Long companyQna_id){
        Optional<CompanyQna> preInterviews = companyQnaRepository.findCompanyQnaByIdAndInterviewgroupId(companyQna_id, interviewGroupId);
        if (preInterviews.isEmpty()) {
            return null;
        }

        CompanyQnaDTO companyQnaDTO = new CompanyQnaDTO(preInterviews.get());
        return companyQnaDTO;
    }

    public List<CompanyQnaDTO> readAll(Long companyId, Long interviewGroupId){
        Optional<InterviewGroup> interviewGroup = interviewGroupRepository.findById(interviewGroupId);
        if (interviewGroup.isEmpty() || interviewGroup.get().getCompany().getId() != companyId) {
            return null;
        }
        List<CompanyQnaDTO> companyQnaDTOS = CompanyQnaDTO.convertToDto(interviewGroup.get().getCompanyQnas());
        return companyQnaDTOS;
    }

    public Page<CompanyQnaDTO> readAllPageable(Long companyId, Long interviewGroupId, Pageable pageable){
        Optional<InterviewGroup> interviewGroup = interviewGroupRepository.findById(interviewGroupId);
        if (interviewGroup.isEmpty() || interviewGroup.get().getCompany().getId() != companyId) {
            return null;
        }

        Page<CompanyQna> preInterviews = companyQnaRepository.findByInterviewgroupId(interviewGroupId, pageable);
        Page<CompanyQnaDTO> preInterviewDTOS = CompanyQnaDTO.toDtoPage(preInterviews);
        return preInterviewDTOS;
    }

    @Transactional
    public CompanyQnaDTO update(Long companyId, Long interviewGroupId, Long companyQna_id, CompanyQnaDTO companyQnaDTO){
        Optional<CompanyQna> preInterviews = companyQnaRepository.findCompanyQnaByIdAndInterviewgroupId(companyQna_id, interviewGroupId);
        if (preInterviews.isEmpty() || preInterviews.get().getInterviewgroup().getCompany().getId() != companyId){
            return null;
        }

        CompanyQna companyqna = preInterviews.get();
        companyqna.setDtoToObject(companyQnaDTO); // update

        CompanyQnaDTO createdCompanyQnaDTO = new CompanyQnaDTO(companyqna);
        return createdCompanyQnaDTO;
    }

    @Transactional
    public CompanyQnaDTO delete(Long companyId, Long interviewGroupId, Long companyQna_id){
        Optional<CompanyQna> preInterviews = companyQnaRepository.findCompanyQnaByIdAndInterviewgroupId(companyQna_id, interviewGroupId);
        if (preInterviews.isEmpty() || preInterviews.get().getInterviewgroup().getCompany().getId() != companyId){
            return null;
        }

        CompanyQna companyqna = preInterviews.get();
        companyQnaRepository.delete(companyqna);

        CompanyQnaDTO companyQnaDTO = new CompanyQnaDTO(companyqna);
        return companyQnaDTO;
    }
}
