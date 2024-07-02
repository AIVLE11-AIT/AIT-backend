package aivle.ait.Controller;

import aivle.ait.Dto.FileDTO;
import aivle.ait.Dto.InterviewerDTO;
import aivle.ait.Security.Auth.CustomUserDetails;
import aivle.ait.Service.ActionResultService;
import aivle.ait.Service.FileService;
import aivle.ait.Service.InterviewerService;
import aivle.ait.Service.VoiceResultService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/interviewGroup/{interviewGroup_id}/interviewer/{interviewer_id}/file", produces = MediaType.APPLICATION_JSON_VALUE)
public class FileController {
    private final FileService fileService;
    private final VoiceResultService voiceResultService;
    private final ActionResultService actionResultService;

    @PostConstruct
    // files 폴더 생성
    public void init() {
        Path uploadPath = Paths.get("files");
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectory(uploadPath);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @PostMapping("/{companyQna_id}")
    public ResponseEntity<FileDTO> uploadFile(@PathVariable("interviewGroup_id") Long interviewGroup_id,
                                                  @PathVariable("interviewer_id") Long interview_id,
                                                  @PathVariable("companyQna_id") Long companyQna_id,
                                                  @RequestParam("file") MultipartFile file) {
        try {
            // 파일 저장 경로 설정
            Path path = Paths.get("files");
            Path uploadPath = path.resolve(String.valueOf(interviewGroup_id));

            if (!Files.exists(uploadPath)) { // /files/interviewGroupid 폴더 생성
                Files.createDirectory(uploadPath);
            }

            // 지원자 정보로 파일 이름 변경 예정
            String originalFileName = file.getOriginalFilename();
            String[] parts = originalFileName.split("\\.");
            String fileExtension = parts[parts.length - 1];
            String newFileName = String.format("%s_%s.%s", interview_id, companyQna_id, fileExtension);

            Path filePath = Paths.get("files/" + String.valueOf(interviewGroup_id), newFileName);
            Files.write(filePath, file.getBytes()); // files 로컬 폴더에 저장

            FileDTO created_file = fileService.save(interviewGroup_id, interview_id, companyQna_id, filePath.toString());

            // 영상분석
            voiceResultService.sendToVoice(created_file);
            actionResultService.sendToAction(created_file);

            return ResponseEntity.ok(created_file);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

}
