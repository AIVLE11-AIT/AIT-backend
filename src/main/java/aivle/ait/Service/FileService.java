package aivle.ait.Service;

import aivle.ait.Dto.FileDTO;
import aivle.ait.Entity.CompanyQna;
import aivle.ait.Entity.File;
import aivle.ait.Entity.Interviewer;
import aivle.ait.Repository.CompanyQnaRepository;
import aivle.ait.Repository.FileRepository;
import aivle.ait.Repository.InterviewerRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileService {
    private final CompanyQnaRepository companyQnaRepository;
    private final InterviewerRepository interviewerRepository;
    private final FileRepository fileRepository;

    private final VoiceResultService voiceResultService;
    private final ActionResultService actionResultService;

    @Transactional
    public FileDTO save(Long interviewGroupId,
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

        File createdFile = fileRepository.save(file);
        FileDTO createFileDto = new FileDTO(createdFile);

        return createFileDto;
    }

    public List<FileDTO> loadFiles(Long interviewer_id) {
        Optional<List<File>> filesOptional = fileRepository.findByInterviewerId(interviewer_id);
        if (filesOptional.isEmpty()) return null;

        List<File> files = filesOptional.get();
        List<FileDTO> fileDTOS = new ArrayList<>();

        for(File file: files) {
            System.out.println(file.getId() + file.getVideo_path());
            FileDTO dto = new FileDTO(file);
            fileDTOS.add(dto);
        }
        return fileDTOS;
    }

    @Async
    public void sendToActionAndVoice(List<FileDTO> fileDTOS) {
        for (FileDTO file : fileDTOS) {
            // 파일 처리 로직 예제
            voiceResultService.sendToVoice(file);
            actionResultService.sendToAction(file);
            System.out.println("Send success: " + file.getVideo_path());
        }

        try {
            Thread.sleep(5000); // 5초 후 함수 종료
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("finish send!");
    }
}
