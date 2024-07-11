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
//        CompletableFuture<String> interviewerAnswer = voiceResultService.sendToVoice(fileDTO);
//        actionResultService.sendToAction(fileDTO);
        String testAnswer = "안녕하세요. KT 인재상 중에서 저에게 가장 적합하다고 생각하는 부분은 도전 정신입니다. 저는 항상 새로운 도전과 목표를 설정하며 성장을 추구하는 사람입니다. 예를 들어, 대학 시절에는 학업과 병행하여 다양한 프로젝트와 인턴십에 참여하면서 실무 경험을 쌓았습니다. 이러한 경험들은 저의 문제 해결 능력과 창의성을 크게 향상시켜 주었고, 이는 곧 KT의 혁신적인 사업 환경에서도 큰 도움이 될 것이라고 생각합니다. 또한, 저는 변화에 빠르게 적응하며 새로운 기술과 트렌드를 학습하는 것을 즐깁니다. KT는 빠르게 변화하는 ICT 산업의 선두주자로서 지속적인 혁신과 발전을 추구하는 기업입니다. 따라서 저의 도전 정신과 학습 능력은 KT의 목표와 매우 부합한다고 생각합니다. 마지막으로, 도전 정신은 팀워크와 협업에서도 중요한 요소라고 생각합니다. 저는 다양한 팀 프로젝트를 통해 협력하고 소통하는 능력을 키워왔으며, 이를 바탕으로 KT의 다양한 부서와 협력하여 성공적인 결과를 만들어낼 자신이 있습니다. 감사합니다.";

        // 공통 질문일 경우
        // CompletableFuture를 사용하면 interviewerAnswer가 값은 리턴 받을 때까지 기다림
        if (fileDTO.getIsGroup()) {
            contextResultService.sendToContextByCompanyQna(fileDTO, qnaId, testAnswer);
//            contextResultService.sendToContextByCompanyQna(fileDTO, qnaId, interviewerAnswer.get());
        }
        // 자소서 기반 질문일 경우
        else {
            contextResultService.sendToContextByInterviewerQna(fileDTO, qnaId, interviewerId, testAnswer);
//            contextResultService.sendToContextByInterviewerQna(fileDTO, qnaId, interviewerId, interviewerAnswer.get());
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
