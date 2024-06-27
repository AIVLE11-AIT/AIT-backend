package aivle.ait.Service;

import aivle.ait.Dto.PreInterviewDTO;
import aivle.ait.Entity.InterviewGroup;
import aivle.ait.Entity.PreInterview;
import aivle.ait.Repository.InterviewGroupRepository;
import aivle.ait.Repository.PreInterviewRepository;
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
public class PreInterviewService {
    private final InterviewGroupRepository interviewGroupRepository;
    private final PreInterviewRepository preInterviewRepository;

    @Transactional
    public PreInterviewDTO create(Long companyId, Long interviewGroupId, PreInterviewDTO preInterviewDTO){
        Optional<InterviewGroup> interviewGroup = interviewGroupRepository.findById(interviewGroupId);

        if (interviewGroup.isEmpty() || interviewGroup.get().getCompany().getId() != companyId) {
            return null;
        }

        PreInterview preInterview = new PreInterview();
        preInterview.setDtoToObject(preInterviewDTO);
        preInterview.setInterviewgroup(interviewGroup.get());

        PreInterview createdPreInterview = preInterviewRepository.save(preInterview);
        PreInterviewDTO createdPreInterviewDTO = new PreInterviewDTO(createdPreInterview);

        return createdPreInterviewDTO;
    }

    public PreInterviewDTO readOne(Long companyId, Long interviewGroupId, Long preInterviewId){
        Optional<PreInterview> preInterviews = preInterviewRepository.findPreInterviewByIdAndInterviewgroupId(preInterviewId, interviewGroupId);
        if (preInterviews.isEmpty() || preInterviews.get().getInterviewgroup().getCompany().getId() != companyId) {
            return null;
        }

        PreInterviewDTO preInterviewDTO = new PreInterviewDTO(preInterviews.get());
        return preInterviewDTO;
    }

    public List<PreInterviewDTO> readAll(Long companyId, Long interviewGroupId){
        Optional<InterviewGroup> interviewGroup = interviewGroupRepository.findById(interviewGroupId);
        if (interviewGroup.isEmpty() || interviewGroup.get().getCompany().getId() != companyId) {
            return null;
        }
        List<PreInterviewDTO> preInterviewDTOS = PreInterviewDTO.convertToDto(interviewGroup.get().getPre_interviews());
        return preInterviewDTOS;
    }

    public Page<PreInterviewDTO> readAllPageable(Long companyId, Long interviewGroupId, Pageable pageable){
        Optional<InterviewGroup> interviewGroup = interviewGroupRepository.findById(interviewGroupId);
        if (interviewGroup.isEmpty() || interviewGroup.get().getCompany().getId() != companyId) {
            return null;
        }

        Page<PreInterview> preInterviews = preInterviewRepository.findByInterviewgroupId(interviewGroupId, pageable);
        Page<PreInterviewDTO> preInterviewDTOS = PreInterviewDTO.toDtoPage(preInterviews);
        return preInterviewDTOS;
    }

    @Transactional
    public PreInterviewDTO update(Long companyId, Long interviewGroupId, Long preInterviewId, PreInterviewDTO preInterviewDTO){
        Optional<PreInterview> preInterviews = preInterviewRepository.findPreInterviewByIdAndInterviewgroupId(preInterviewId, interviewGroupId);
        if (preInterviews.isEmpty() || preInterviews.get().getInterviewgroup().getCompany().getId() != companyId){
            return null;
        }

        PreInterview preInterview = preInterviews.get();
        preInterview.setDtoToObject(preInterviewDTO); // update

        PreInterviewDTO createdPreInterviewDTO = new PreInterviewDTO(preInterview);
        return createdPreInterviewDTO;
    }

    @Transactional
    public PreInterviewDTO delete(Long companyId, Long interviewGroupId, Long preInterviewId){
        Optional<PreInterview> preInterviews = preInterviewRepository.findPreInterviewByIdAndInterviewgroupId(preInterviewId, interviewGroupId);
        if (preInterviews.isEmpty() || preInterviews.get().getInterviewgroup().getCompany().getId() != companyId){
            return null;
        }

        PreInterview preInterview = preInterviews.get();
        preInterviewRepository.delete(preInterview);

        PreInterviewDTO preInterviewDTO = new PreInterviewDTO(preInterview);
        return preInterviewDTO;
    }
}
