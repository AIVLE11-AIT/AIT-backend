package aivle.ait.Controller;

import aivle.ait.Dto.FileDTO;
import aivle.ait.Dto.InterviewerDTO;
import aivle.ait.Security.Auth.CustomUserDetails;
import aivle.ait.Service.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.connector.Response;
import org.apache.tomcat.util.descriptor.web.ContextService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Repository;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/interviewGroup/{interviewGroup_id}/interviewer/{interviewer_id}/file", produces = MediaType.APPLICATION_JSON_VALUE)
public class FileController {
    private final FileService fileService;
    private final VoiceResultService voiceResultService;
    private final ActionResultService actionResultService;
    private final ContextResultService contextResultService;

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

    @PostMapping("/companyQna/{companyQna_id}")
    public ResponseEntity<String> uploadFileByCompanyQna(@PathVariable("interviewGroup_id") Long interviewGroup_id,
                                                  @PathVariable("interviewer_id") Long interviewer_id,
                                                  @PathVariable("companyQna_id") Long companyQna_id,
                                                  @RequestParam("file") MultipartFile file) {
        try {
            if (fileService.checkByCompanyQna(interviewer_id, companyQna_id)) {
                fileService.deleteByCompanyQna(interviewer_id, companyQna_id);
            }

            String filePath = saveFile(true, interviewGroup_id, interviewer_id, companyQna_id, file);

            FileDTO created_file = fileService.saveByCompanyQna(interviewGroup_id, interviewer_id, companyQna_id, filePath);

            // 비동기로 영상분석 서비스 호출
            asyncProcessVideoAnalysis(companyQna_id, interviewer_id, created_file);

            return ResponseEntity.ok("save success!");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // 자소서 기반 질문인지 구분 필요
    @PostMapping("/interviewerQna/{interviewerQna_id}")
    public ResponseEntity<String> uploadFileByInterviewerQna(@PathVariable("interviewGroup_id") Long interviewGroup_id,
                                                         @PathVariable("interviewer_id") Long interviewer_id,
                                                         @PathVariable("interviewerQna_id") Long interviewerQna_id,
                                                         @RequestParam("file") MultipartFile file) {
        try {
            if (fileService.checkByInterviewerQna(interviewerQna_id)) {
                fileService.deleteByInterviewerQna(interviewerQna_id);
            }
            String filePath = saveFile(false, interviewGroup_id, interviewer_id, interviewerQna_id, file);

            FileDTO created_file = fileService.saveByInterviewerQna(interviewGroup_id, interviewer_id, interviewerQna_id, filePath);

            // 비동기로 영상분석 서비스 호출
            asyncProcessVideoAnalysis(interviewerQna_id, interviewer_id, created_file);

            return ResponseEntity.ok("save success!");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // 파일 저장 기능 메서드로 분리
    public String saveFile(boolean isGroup, Long interviewGroup_id, Long interviewer_id, Long qna_id, MultipartFile file) throws IOException {
        // 파일 저장 경로 설정
        Path path = Paths.get("files");
        Path path2 = path.resolve("videos");
        Path path3 = path2.resolve(String.valueOf(interviewGroup_id));
        Path uploadPath = path3.resolve(String.valueOf(interviewer_id));


        if (!Files.exists(path2)) { // /files/videos 폴더 생성
            Files.createDirectory(path2);
        }

        if (!Files.exists(path3)) { // /files/videos/{interviewGroup_id}
            Files.createDirectory(path3);
        }
        if (!Files.exists(uploadPath)) { // /files/videos/{interviewGroup_id}/interview_id 폴더 생성
            Files.createDirectory(uploadPath);
        }

        // 지원자 정보로 파일 이름 변경 예정
        String originalFileName = file.getOriginalFilename();
        String[] parts = originalFileName.split("\\.");
        String fileExtension = parts[parts.length - 1];

        // publicOrPrivate 기업 질문인지 자소서 기반 질문인지 구분. 파일 이름 중복될 위험 방지. ex) 기업 공통 질문: 1_public-1.mp4, 개인 자소서 기반 질문: 1_private-1.mp4
        String newFileName;
        if (isGroup)
            newFileName = String.format("%s_public-%s.%s", interviewer_id, qna_id, fileExtension);
        else
            newFileName = String.format("%s_private-%s.%s", interviewer_id, qna_id, fileExtension);

        Path filePath = Paths.get(uploadPath.toString(), newFileName);
        Files.write(filePath, file.getBytes()); // files 로컬 폴더에 저장

        return filePath.toString();
    }

    // 비동기 메서드로 영상분석 서비스 호출
    @Async
    public void asyncProcessVideoAnalysis(Long qnaId, Long interviewerId, FileDTO fileDTO) throws ExecutionException, InterruptedException {
        CompletableFuture<String> interviewerAnswer = voiceResultService.sendToVoice(fileDTO);
        actionResultService.sendToAction(fileDTO);

        // 공통 질문일 경우
        // CompletableFuture를 사용하면 interviewerAnswer가 값은 리턴 받을 때까지 기다림
        if (fileDTO.getIsGroup()) {
            contextResultService.sendToContextByCompanyQna(fileDTO, qnaId, interviewerAnswer.get());
        }
        // 자소서 기반 질문일 경우
        else {
            contextResultService.sendToContextByInterviewerQna(fileDTO, qnaId, interviewerId, interviewerAnswer.get());
        }
    }

    // 기업 공통 질문 면접 영상 불러오기
    @GetMapping(value="/companyQna/{companyQna_id}/read")
    public ResponseEntity<Resource> readFileByCompanyQna(@PathVariable("interviewGroup_id") Long interviewGroup_id,
                     @PathVariable("interviewer_id") Long interviewer_id,
                     @PathVariable("companyQna_id") Long companyQna_id,
                     @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Resource videoForCompanyQna = fileService.readFileByCompanyQna(customUserDetails.getCompany().getId(), interviewer_id, companyQna_id);

        if (videoForCompanyQna == null) return ResponseEntity.badRequest().body(null);
        else {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+videoForCompanyQna.getFilename()+"\"");
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(videoForCompanyQna);
        }
    }

    // 인터뷰 개인 질문 면접 영상 불러오기
    @GetMapping(value="/interviewerQna/{interviewerQna_id}/read")
    public ResponseEntity<Resource> readFileByInterviewerQna(@PathVariable("interviewer_id") Long interviewer_id,
                                                             @PathVariable("interviewerQna_id") Long interviewerQna_id,
                                                             @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Resource videoForInterviewerQna = fileService.readFileByInterviewerQna(interviewerQna_id, interviewer_id, customUserDetails.getCompany().getId());

        if (videoForInterviewerQna == null) return ResponseEntity.badRequest().body(null);
        else {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+videoForInterviewerQna.getFilename()+"\"");
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(videoForInterviewerQna);
        }
    }


}
