package aivle.ait.Service;

import aivle.ait.Dto.InterviewGroupDTO;
import aivle.ait.Entity.Company;
import aivle.ait.Entity.InterviewGroup;
import aivle.ait.Repository.CompanyRepository;
import aivle.ait.Repository.InterviewGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InterviewGroupService {
    private final CompanyRepository companyRepository;
    private final InterviewGroupRepository interviewGroupRepository;

    @Transactional
    public InterviewGroupDTO create(Long companyId, InterviewGroupDTO interviewGroupDTO){
        Optional<Company> company = companyRepository.findById(companyId);

        if (company.isEmpty()) {
            return null;
        }

        InterviewGroup interviewGroup = new InterviewGroup();
        interviewGroup.setDtoToObject(interviewGroupDTO);
        interviewGroup.setCompany(company.get());

        InterviewGroup createdQuestion = interviewGroupRepository.save(interviewGroup);
        InterviewGroupDTO createdInterviewGroupDTO = new InterviewGroupDTO(createdQuestion);

        return createdInterviewGroupDTO;
    }

    public InterviewGroupDTO readOne(Long company_id, Long interviewGroup_id){
        Optional<InterviewGroup> interviewGroup = interviewGroupRepository.findById(interviewGroup_id);
        if (interviewGroup.isEmpty() || interviewGroup.get().getCompany().getId() != company_id) {
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
        Optional<Company> company = companyRepository.findById(companyId);
        if (company.isEmpty()) {
            return null;
        }
        Page<InterviewGroup> interviewGroups = interviewGroupRepository.findByCompany(company.get(), pageable);
        Page<InterviewGroupDTO> interviewGroupDTOS = InterviewGroupDTO.toDtoPage(interviewGroups);
        return interviewGroupDTOS;
    }

    @Transactional
    public InterviewGroupDTO update(Long companyId, Long interviewGroupId, InterviewGroupDTO interviewGroupDTO){
        Optional<InterviewGroup> interviewGroups = interviewGroupRepository.findById(interviewGroupId);
        if (interviewGroups.isEmpty() || interviewGroups.get().getCompany().getId() != companyId){
            return null;
        }

        InterviewGroup interviewGroup = interviewGroups.get();
        interviewGroup.setDtoToObject(interviewGroupDTO); // update

        InterviewGroupDTO createdInterviewGroupDTO = new InterviewGroupDTO(interviewGroup);
        return createdInterviewGroupDTO;
    }

    @Transactional
    public InterviewGroupDTO delete(Long companyId, Long interviewGroupId){
        Optional<InterviewGroup> interviewGroups = interviewGroupRepository.findById(interviewGroupId);
        if (interviewGroups.isEmpty() || interviewGroups.get().getCompany().getId() != companyId){
            return null;
        }

        InterviewGroup interviewGroup = interviewGroups.get();
        interviewGroupRepository.delete(interviewGroup);

        InterviewGroupDTO interviewGroupDTO = new InterviewGroupDTO(interviewGroup);
        return interviewGroupDTO;
    }
}
