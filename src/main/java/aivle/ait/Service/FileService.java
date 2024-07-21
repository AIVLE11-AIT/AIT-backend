package aivle.ait.Service;

import aivle.ait.Dto.FileDTO;
import aivle.ait.Entity.*;
import aivle.ait.Repository.CompanyQnaRepository;
import aivle.ait.Repository.FileRepository;
import aivle.ait.Repository.InterviewerQnaRepository;
import aivle.ait.Repository.InterviewerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
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

    public boolean checkByCompanyQna(Long interviewerId,
                                     Long companyQnaId) {
        Optional<File> file = fileRepository.findByInterviewerIdAndCompanyQnaId(interviewerId, companyQnaId);

        return file.isPresent();
    }

    public boolean checkByInterviewerQna(Long interviewerQnaId) {
        Optional<File> file = fileRepository.findByInterviewerQnaId(interviewerQnaId);

        return file.isPresent();
    }

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
                                        Long interviewerQnaId,
                                        String videoPath) {

        Optional<InterviewerQna> interviewerQna = interviewerQnaRepository.findInterviewerByIdAndInterviewerId(interviewerQnaId, interviewerId);
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
        FileDTO createdFileDto = new FileDTO(createdFile);

        return createdFileDto;
    }

    @Transactional
    public FileDTO deleteByCompanyQna(Long interviewerId, Long companyQnaId) {
        Optional<File> file = fileRepository.findByInterviewerIdAndCompanyQnaId(interviewerId, companyQnaId);
        if (file.isEmpty()){
            System.out.println("null");
            return null;
        }

        fileRepository.delete(file.get());

        return new FileDTO(file.get());
    }

    @Transactional
    public void deleteByInterviewerQna(Long interviewerQnaId) {
        Optional<File> file = fileRepository.findByInterviewerQnaId(interviewerQnaId);
        if (file.isEmpty()){
            System.out.println("null");
            return;
        }

        File curFile = file.get();
        fileRepository.delete(curFile);
        return;
    }

    // 기업 공통 문항에 대한 면접 영상을 조회
    public Resource readFileByCompanyQna(Long companyId, Long interviewerId, Long companyQnaId) {
        Optional<File> fileOptional = fileRepository.findByInterviewerIdAndCompanyQnaId(interviewerId, companyQnaId);
        if (fileOptional.isEmpty() || fileOptional.get().getInterviewer().getInterviewgroup().getCompany().getId() != companyId) {
            System.out.println("면접 영상 없음 or companyId 불일치");
            return null;
        }

        File file = fileOptional.get();
        String filePath = file.getVideo_path();

        Resource resource = read(filePath);
        if (resource == null) {
            System.out.println("영상 없음");
            return null;
        }
        return resource;
    }

    public Resource readFileByInterviewerQna(Long interviewerQnaId, Long interviewerId, Long companyId) {
        Optional<File> fileOptional = fileRepository.findByInterviewerQnaId(interviewerQnaId);
        if (fileOptional.isEmpty() || fileOptional.get().getInterviewer().getInterviewgroup().getCompany().getId() != companyId) {
            System.out.println("면접 영상 없음 or companyId 불일치");
            return null;
        }

        File file = fileOptional.get();
        String filePath = file.getVideo_path();

        Resource resource = read(filePath);
        if (resource == null) {
            System.out.println("영상 없음");
            return null;
        }
        return resource;
    }

    public Resource read(String filePath) {
        FileSystemResource resource = new FileSystemResource(new java.io.File(filePath));

        if (!resource.exists()) return null;

        return resource;
    }

    public VisualQnADTO readQnaByCompanyQna(Long interviewerId, Long companyQnaId) {
        Optional<CompanyQna> companyQna = companyQnaRepository.findCompanyQnaByIdAndInterviewerId(companyQnaId, interviewerId);
        Optional<File> file = fileRepository.findByInterviewerIdAndCompanyQnaId(interviewerId, companyQnaId);
        if (companyQna.isEmpty()){
            System.out.println("companyQna ID가 유효하지 않음.");
            return null;
        }
        if (file.isEmpty()){
            System.out.println("file ID가 유효하지 않음.");
            return null;
        }

        VisualQnADTO visualQnADTO = new VisualQnADTO(companyQna.get().getQuestion(), file.get().getAnswer());
        return visualQnADTO;
    }

    public VisualQnADTO readQnaByInterviewerQna(Long interviewerId, Long interviewerQnaId) {
        Optional<InterviewerQna> interviewerQna = interviewerQnaRepository.findInterviewerByIdAndInterviewerId(interviewerQnaId, interviewerId);
        Optional<File> file = fileRepository.findByInterviewerQnaId(interviewerQnaId);
        if (interviewerQna.isEmpty()){
            System.out.println("interviewer ID가 유효하지 않음.");
            return null;
        }
        if (file.isEmpty()){
            System.out.println("interviewerQna ID가 유효하지 않음.");
            return null;
        }

        VisualQnADTO visualQnADTO = new VisualQnADTO(interviewerQna.get().getQuestion(), file.get().getAnswer());
        return visualQnADTO;
    }
}
