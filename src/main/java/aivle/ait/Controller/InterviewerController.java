package aivle.ait.Controller;

import aivle.ait.Dto.InterviewerDTO;
import aivle.ait.Entity.Interviewer;
import aivle.ait.Security.Auth.CustomUserDetails;
import aivle.ait.Service.InterviewGroupService;
import aivle.ait.Service.InterviewerService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/interviewGroup/{interviewGroup_id}/interviewer", produces = MediaType.APPLICATION_JSON_VALUE)
public class InterviewerController {
    private final InterviewerService interviewerService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    public ResponseEntity<InterviewerDTO> create(@RequestBody InterviewerDTO interviewerDTO,
                                                  @PathVariable("interviewGroup_id") Long interviewGroup_id,
                                                  @AuthenticationPrincipal CustomUserDetails customUserDetails){
        InterviewerDTO createdInterviewerDTO = interviewerService.create(customUserDetails.getCompany().getId(), interviewGroup_id, interviewerDTO);

        if (createdInterviewerDTO != null){
            return ResponseEntity.ok(createdInterviewerDTO);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{interviewer_id}")
    public ResponseEntity<InterviewerDTO> read(@PathVariable("interviewGroup_id") Long interviewGroup_id,
                                                @PathVariable("interviewer_id") Long preInterview_id,
                                                @AuthenticationPrincipal CustomUserDetails customUserDetails){
        InterviewerDTO InterviewerDTO = interviewerService.readOne(customUserDetails.getCompany().getId(), interviewGroup_id, preInterview_id);

        if (InterviewerDTO != null){
            return ResponseEntity.ok(InterviewerDTO);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/readAll")
    public ResponseEntity<List<InterviewerDTO>> readAll(@PathVariable("interviewGroup_id") Long interviewGroup_id,
                                                         @AuthenticationPrincipal CustomUserDetails customUserDetails){
        List<InterviewerDTO> InterviewerDTOS = interviewerService.readAll(customUserDetails.getCompany().getId(), interviewGroup_id);

        if (InterviewerDTOS != null){
            return ResponseEntity.ok(InterviewerDTOS);
        }
        else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 내 면접 그룹들만 페이지 별로 불러오기 ex) localhost:8888/interviewGroup/{interviewGroup_id}/preInterview/read?page=0&size=5
    @GetMapping("/read")
    public ResponseEntity<Page<InterviewerDTO>> readPage(@PageableDefault(size = 5) Pageable pageable,
                                                          @PathVariable("interviewGroup_id") Long interviewGroup_id,
                                                          @AuthenticationPrincipal CustomUserDetails customUserDetails){
        Page<InterviewerDTO> InterviewerDTOS = interviewerService.readAllPageable(customUserDetails.getCompany().getId(), interviewGroup_id, pageable);

        if (!InterviewerDTOS.isEmpty()){
            return ResponseEntity.ok(InterviewerDTOS);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/readAll/{isPass}")
    public ResponseEntity<?> readAllByIsPass(@PathVariable("interviewGroup_id") Long interviewGroup_id,
                                            @PathVariable("isPass") Boolean isPass,
                                            @AuthenticationPrincipal CustomUserDetails customUserDetails){
        List<InterviewerDTO> InterviewerDTOS = interviewerService.readAllByIsPass(isPass, customUserDetails.getCompany().getId(), interviewGroup_id);

        if (InterviewerDTOS == null){
            return ResponseEntity.badRequest().body("조회하는 인터뷰 그룸과 로그인 아이디가 일치 하지 않음.");
        }

        return ResponseEntity.ok(InterviewerDTOS);
    }

    @PutMapping("/{interviewer_id}/update")
    public ResponseEntity<InterviewerDTO> update(@PathVariable("interviewGroup_id") Long interviewGroup_id,
                                                  @PathVariable("interviewer_id") Long preInterview_id,
                                                  @RequestBody InterviewerDTO interviewerDTO,
                                                  @AuthenticationPrincipal CustomUserDetails customUserDetails){
        InterviewerDTO updatedInterviewerDTO = interviewerService.update(customUserDetails.getCompany().getId(), interviewGroup_id, preInterview_id, interviewerDTO);

        if (updatedInterviewerDTO != null){
            return ResponseEntity.ok(updatedInterviewerDTO);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{interviewer_id}/delete")
    public ResponseEntity<InterviewerDTO> delete(@PathVariable("interviewGroup_id") Long interviewGroup_id,
                                                  @PathVariable("interviewer_id") Long preInterview_id,
                                                  @AuthenticationPrincipal CustomUserDetails customUserDetails){
        InterviewerDTO deletedInterviewerDTO = interviewerService.delete(customUserDetails.getCompany().getId(), interviewGroup_id, preInterview_id);

        if (deletedInterviewerDTO != null){
            return ResponseEntity.ok(deletedInterviewerDTO);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 면접 그룹에 속한 지원자 전체에게 면접 링크 메일 전송
    // 이메일 전송 여부 체크
    @GetMapping("/sendEmail")
    public ResponseEntity<List<InterviewerDTO>> sendEmail(@PathVariable("interviewGroup_id") Long interviewGroup_id,
                                                    @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        List<InterviewerDTO> InterviewerDTOs = interviewerService.readAll(customUserDetails.getCompany().getId(), interviewGroup_id);

        if (InterviewerDTOs != null) {
            for (InterviewerDTO interviewerDTO : InterviewerDTOs) {
                Long interviewerId = interviewerDTO.getId();
                String url = "http://localhost:8080/" + customUserDetails.getCompany().getId() + "/" + interviewGroup_id + "/" + interviewerId;
                try {
                    boolean send = interviewerService.sendEmail(interviewerDTO, customUserDetails.getCompany().getId(), interviewGroup_id, url);
                    if (!send) return ResponseEntity.badRequest().body(null);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                }
            }
        } else {
            return ResponseEntity.badRequest().body(null);
        }

        return ResponseEntity.ok(InterviewerDTOs);
    }

    // 사진 파일 전송
    @PostMapping("/{interviewer_id}/image")
    public ResponseEntity<?> sendImage(@PathVariable("interviewGroup_id") Long interviewGroup_id,
                                       @PathVariable("interviewer_id") Long interview_id,
                                       @RequestPart(value="file") MultipartFile file) {
        try {
            Path path = Paths.get("files");
            Path path2 = path.resolve("images");
            Path path3 = path2.resolve(String.valueOf(interviewGroup_id));
            Path uploadPath = path3.resolve(String.valueOf(interview_id));

            if (!Files.exists(path2)) { // /files/images 폴더 생성
                Files.createDirectory(path2);
            }

            if (!Files.exists(path3)) { // /files/images/{interviewGroup_id}
                Files.createDirectory(path3);
            }
            if (!Files.exists(uploadPath)) { // /files/images/{interviewGroup_id}/{interview_id} 폴더 생성
                Files.createDirectory(uploadPath);
            }

            // 지원자 정보로 파일 이름 변경 예정
            String originalFileName = file.getOriginalFilename();
            String[] parts = originalFileName.split("\\.");
            String fileExtension = parts[parts.length - 1];
            String newFileName = String.format("%s_%s.%s", interviewGroup_id, interview_id, fileExtension);

            Path filePath = Paths.get(uploadPath.toString(), newFileName);
            Files.write(filePath, file.getBytes()); // files 로컬 폴더에 저장

            InterviewerDTO interviewerDTO = interviewerService.sendImagePath(interviewGroup_id, interview_id, filePath.toString());

            return ResponseEntity.ok(interviewerDTO);

        } catch(IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    // 면접자 이미지 전송
    @GetMapping(value="/{interviewer_id}/image/read")
    public ResponseEntity<Resource> downloadImage(@PathVariable("interviewGroup_id") Long interviewGroup_id,
                                                  @PathVariable("interviewer_id") Long interviewer_id,
                                                  @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Resource image = interviewerService.downloadImage(interviewer_id, customUserDetails.getCompany().getId());

        if (image == null) return ResponseEntity.badRequest().body(null);
        else {
            String fileName = getFileName(image);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+fileName+"\"");
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(image);
        }
    }

    private String getFileName(Resource resource) {
        if (resource instanceof UrlResource) {
            return new File(((UrlResource) resource).getURL().getPath()).getName();
        } else if (resource instanceof FileSystemResource) {
            return new File(((FileSystemResource) resource).getPath()).getName();
        } else {
            return "image.jpg"; // or a default filename
        }
    }
}
