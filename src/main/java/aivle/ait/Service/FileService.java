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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileService {
    private final CompanyQnaRepository companyQnaRepository;
    private final InterviewerRepository interviewerRepository;
    private final FileRepository fileRepository;

    @Transactional
    public FileDTO save(Long interviewGroupId,
                        Long interviewerId,
                        Long companyQnaId,
                        String videoPath) {

        Optional<CompanyQna> companyQna = companyQnaRepository.findCompanyQnaByIdAndInterviewgroupId(companyQnaId, interviewGroupId);
        Optional<Interviewer> interviewer = interviewerRepository.findInterviewerByIdAndInterviewgroupId(interviewerId, interviewGroupId);
        if (companyQna.isEmpty() || interviewer.isEmpty()) {
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
}
