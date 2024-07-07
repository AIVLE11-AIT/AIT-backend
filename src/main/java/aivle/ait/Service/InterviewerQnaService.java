package aivle.ait.Service;

import aivle.ait.Dto.InterviewerQnaDTO;
import aivle.ait.Entity.Interviewer;
import aivle.ait.Entity.InterviewerQna;
import aivle.ait.Repository.InterviewerQnaRepository;
import aivle.ait.Repository.InterviewerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InterviewerQnaService {
    private final InterviewerQnaRepository interviewerQnaRepository;
    private final InterviewerRepository interviewerRepository;

    public InterviewerQnaDTO readOne(Long interviewerQnaId, Long interviewerId, Long interviewGroupId){
        Optional<InterviewerQna> interviewerQnas = interviewerQnaRepository.findInterviewerByIdAndInterviewerId(interviewerQnaId, interviewerId);
        if (interviewerQnas.isEmpty() || interviewerQnas.get().getInterviewer().getInterviewgroup().getId() != interviewGroupId) {
            return null;
        }

        InterviewerQnaDTO interviewerQnaDTO = new InterviewerQnaDTO(interviewerQnas.get());
        return interviewerQnaDTO;
    }

    public List<InterviewerQnaDTO> readAll(Long interviewerId, Long interviewGroupId){
        Optional<Interviewer> interviewers = interviewerRepository.findById(interviewerId);
        if (interviewers.isEmpty() || interviewers.get().getInterviewgroup().getId() != interviewGroupId) {
            return null;
        }
        List<InterviewerQnaDTO> interviewerQnaDTOS = InterviewerQnaDTO.convertToDto(interviewers.get().getInterviewerQnas());
        return interviewerQnaDTOS;
    }
}
