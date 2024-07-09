package aivle.ait.Service;

import aivle.ait.Dto.IntroductionVideoDTO;
import aivle.ait.Entity.*;
import aivle.ait.Repository.CompanyRepository;
import aivle.ait.Repository.InterviewGroupRepository;
import aivle.ait.Repository.InterviewerRepository;
import aivle.ait.Repository.IntroductionVideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class IntroductionVideoService {
    private final IntroductionVideoRepository introductionVideoRepository;
    private final InterviewerRepository interviewerRepository;

    @Transactional
    public IntroductionVideoDTO create(Long interviewGroup_id,
                                       Long interviewer_id,
                                       String filePath) {
        Optional<Interviewer> interviewerOptional = interviewerRepository.findInterviewerByIdAndInterviewgroupId(interviewer_id, interviewGroup_id);
        if (interviewerOptional.isEmpty()) {
            System.out.println("introduction video null");
            return null;
        }

        IntroductionVideo introductionVideo = new IntroductionVideo();
        introductionVideo.setVideo_path(filePath);
        introductionVideo.setInterviewer(interviewerOptional.get());

        IntroductionVideo createdVideo = introductionVideoRepository.save(introductionVideo);
        IntroductionVideoDTO createdVideoDto = new IntroductionVideoDTO(createdVideo);
        return createdVideoDto;
    }

    public Resource read(Long companyId, Long interviewer_id) {
        Optional<IntroductionVideo> videoOptional = introductionVideoRepository.findByInterviewerId(interviewer_id);
        if (videoOptional.isEmpty()) {
            System.out.println("자기소개 영상 없음");
            return null;
        }

        IntroductionVideo introductionVideo = videoOptional.get();
        Long realCompanyID = introductionVideo.getInterviewer().getInterviewgroup().getCompany().getId();

        if (realCompanyID != companyId)
            return null;
        File file = new File(introductionVideo.getVideo_path());
        FileSystemResource resource = new FileSystemResource(file);

        if (!resource.exists()) {
            System.out.println("자기소개 영상 없음");
            return null;
        }

        return resource;
    }

    public IntroductionVideoDTO update(Long interviewer_id, String filePath) {
        Optional<IntroductionVideo> videoOptional = introductionVideoRepository.findByInterviewerId(interviewer_id);
        if (videoOptional.isEmpty()) {
            System.out.println("자기소개 영상 없음");
            return null;
        }

        IntroductionVideo introductionVideo = videoOptional.get();
        introductionVideo.setVideo_path(filePath);

        IntroductionVideoDTO updatedVideoDto = new IntroductionVideoDTO(introductionVideo);
        return updatedVideoDto;
    }

    @Transactional
    public IntroductionVideoDTO delete(Long companyId, Long interviewer_id) {
        Optional<IntroductionVideo> videoOptional = introductionVideoRepository.findByInterviewerId(interviewer_id);
        if (videoOptional.isEmpty()) {
            System.out.println("자기소개 영상 없음");
            return null;
        }

        IntroductionVideo deletedVideo = videoOptional.get();
        Long realCompanyId = deletedVideo.getInterviewer().getInterviewgroup().getCompany().getId();
        if (realCompanyId != companyId)
            return null;
        introductionVideoRepository.delete(deletedVideo);

        // 폴더 안의 영상 삭제
        String deletedPath = deletedVideo.getVideo_path();
        boolean isDeleted = deleteFile(deletedPath);

        if (!isDeleted) return null; // 삭제 실패

        IntroductionVideoDTO deletedDto = new IntroductionVideoDTO(deletedVideo);
        return deletedDto;
    }

    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            try {
                return file.delete();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false; // 파일이 존재하지 않음
        }

    }
}
