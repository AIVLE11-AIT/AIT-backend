package aivle.ait.Service;

import aivle.ait.Dto.FileDTO;
import aivle.ait.Entity.CompanyQna;
import aivle.ait.Entity.File;
import aivle.ait.Entity.Interviewer;
import aivle.ait.Entity.InterviewerQna;
import aivle.ait.Repository.CompanyQnaRepository;
import aivle.ait.Repository.FileRepository;
import aivle.ait.Repository.InterviewerQnaRepository;
import aivle.ait.Repository.InterviewerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileService {
    private final CompanyQnaRepository companyQnaRepository;
    private final InterviewerQnaRepository interviewerQnaRepository;
    private final InterviewerRepository interviewerRepository;
    private final FileRepository fileRepository;

    @Transactional
    public FileDTO saveByCompanyQna(Long interviewGroupId,
                                    Long interviewerId,
                                    Long companyQnaId,
                                    String videoPath) {

        Optional<CompanyQna> companyQna = companyQnaRepository.findCompanyQnaByIdAndInterviewgroupId(companyQnaId, interviewGroupId);
        Optional<Interviewer> interviewer = interviewerRepository.findInterviewerByIdAndInterviewgroupId(interviewerId, interviewGroupId);
        if (companyQna.isEmpty() || interviewer.isEmpty()) {
            System.out.println("null");
            return null;
        }

        File file = new File();
        file.setInterviewer(interviewer.get());
        file.setCompanyQna(companyQna.get());
        file.setVideo_path(videoPath);
        file.setIsGroup(true); // 기업 공통 질문 영상인 경우

        File createdFile = fileRepository.save(file);
        FileDTO createFileDto = new FileDTO(createdFile);

        return createFileDto;
    }

    @Transactional
    public FileDTO saveByInterviewerQna(Long interviewGroupId,
                                        Long interviewerId,
                                        Long interviwerQnaId,
                                        String videoPath) {

        Optional<InterviewerQna> interviewerQna = interviewerQnaRepository.findInterviewerByIdAndInterviewerId(interviwerQnaId, interviewerId);
        Optional<Interviewer> interviewer = interviewerRepository.findInterviewerByIdAndInterviewgroupId(interviewerId, interviewGroupId);
        if (interviewerQna.isEmpty() || interviewer.isEmpty()) {
            System.out.println("null");
            return null;
        }

        File file = new File();
        file.setInterviewer(interviewer.get());
        file.setInterviewerQna(interviewerQna.get());
        file.setVideo_path(videoPath);
        file.setIsGroup(false); // 자소서 기반 질문 영상인 경우

        File createdFile = fileRepository.save(file);
        FileDTO createFileDto = new FileDTO(createdFile);

        return createFileDto;
    }

}
