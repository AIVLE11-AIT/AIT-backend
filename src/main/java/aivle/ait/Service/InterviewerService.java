package aivle.ait.Service;

import aivle.ait.Dto.InterviewerDTO;
import aivle.ait.Entity.InterviewGroup;
import aivle.ait.Entity.Interviewer;
import aivle.ait.Repository.InterviewGroupRepository;
import aivle.ait.Repository.InterviewerRepository;
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
public class InterviewerService {
    private final InterviewGroupRepository interviewGroupRepository;
    private final InterviewerRepository interviewerRepository;

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
}
