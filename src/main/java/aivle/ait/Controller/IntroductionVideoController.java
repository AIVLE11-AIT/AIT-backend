package aivle.ait.Controller;

import aivle.ait.Dto.IntroductionVideoDTO;
import aivle.ait.Security.Auth.CustomUserDetails;
import aivle.ait.Service.IntroductionVideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
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
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/interviewGroup/{interviewGroup_id}/interviewer/{interviewer_id}/introduce", produces = MediaType.APPLICATION_JSON_VALUE)
public class IntroductionVideoController {
    private final IntroductionVideoService introductionVideoService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    public ResponseEntity<IntroductionVideoDTO> create(@PathVariable("interviewer_id") Long interviewer_id,
                                                     @PathVariable("interviewGroup_id") Long interviewGroup_id,
                                                     @RequestParam("file")MultipartFile file) {
        try {
            String filePath = saveFile(interviewGroup_id, interviewer_id, file);
            IntroductionVideoDTO introductionVideoDTO = introductionVideoService.create(interviewGroup_id, interviewer_id, filePath);

            if (introductionVideoDTO == null) {
                return ResponseEntity.badRequest().body(null);
            }
            else {
                return ResponseEntity.ok(introductionVideoDTO);
            }

        } catch(IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }

    // 지원자의 자기소개 영상을 불러옴
    @GetMapping("/read")
    public ResponseEntity<Resource> readAll(@PathVariable("interviewer_id") Long interviewer_id,
                                            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Resource video = introductionVideoService.read(customUserDetails.getCompany().getId(), interviewer_id);

        if (video == null) return ResponseEntity.badRequest().body(null);
        else {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+video.getFilename()+"\"");
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(video);
        }
    }

    // 지원자 자기소개 영상 업데이트
    @PutMapping("/update")
    public ResponseEntity<IntroductionVideoDTO> update(@PathVariable("interviewGroup_id") Long interviewGroup_id,
                                                       @PathVariable("interviewer_id") Long interviewer_id,
                                                       @RequestParam("file")MultipartFile file) {
        try {
            String filePath = saveFile(interviewGroup_id, interviewer_id, file);
            IntroductionVideoDTO updatedVideoDto = introductionVideoService.update(interviewer_id, filePath);
            if (updatedVideoDto == null) {
                return ResponseEntity.badRequest().body(null);
            }
            else {
                return ResponseEntity.ok(updatedVideoDto);
            }
        } catch(IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<IntroductionVideoDTO> delete(@PathVariable("interviewer_id") Long interviewer_id,
                                                       @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        IntroductionVideoDTO deletedVideoDto = introductionVideoService.delete(customUserDetails.getCompany().getId(), interviewer_id);

        if (deletedVideoDto != null) {
            return ResponseEntity.ok(deletedVideoDto);
        }
        else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    public String saveFile(Long interviewGroup_id, Long interviewer_id, MultipartFile file) throws IOException {
        // 파일 저장 경로 설정
        // /files/videos/{interviewGroup_id}/{interviewer_id}/introduce 폴더 안에 저장
        Path path = Paths.get("files");
        Path path2 = path.resolve("videos");
        Path path3 = path2.resolve(String.valueOf(interviewGroup_id));
        Path path4 = path3.resolve(String.valueOf(interviewer_id));
        Path uploadPath = path4.resolve("introduce");

        if (!Files.exists(path2)) Files.createDirectory(path2); // files/videos/
        if (!Files.exists(path3)) Files.createDirectory(path3); // files/videos/{interviewGroup_id}
        if (!Files.exists(path4)) Files.createDirectory(path4); // files/videos/{interviewGroup_id}/{interview_id}
        if (!Files.exists(uploadPath)) Files.createDirectory(uploadPath); // files/videos/{interviewGroup_id}/{interview_id}/introduce

        String originalFileName = file.getOriginalFilename();
        String[] parts = originalFileName.split("\\.");
        String fileExtension = parts[parts.length - 1];

        // %s_introduce.mp4
        String newFileName = String.format("%s_introduce.%s", interviewer_id, fileExtension);

        Path filePath = Paths.get(uploadPath.toString(), newFileName);
        Files.write(filePath, file.getBytes()); // 저장

        return filePath.toString();
    }
}
