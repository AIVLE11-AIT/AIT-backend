package aivle.ait.Service;

import aivle.ait.Dto.FileDTO;
import aivle.ait.Dto.InterviewerDTO;
import aivle.ait.Entity.CompanyQna;
import aivle.ait.Entity.File;
import aivle.ait.Entity.InterviewGroup;
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
public class FileService {
    private final ObjectMapper objectMapper;
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

        // 파이썬으로 전송
        int voiceScore = sendToVoice(videoPath);
        int actionScore = sendToAction(videoPath);

        return createFileDto;

    }
    // 음성
    public int sendToVoice(String filePath) {
        try {
            String voiceUrl = "http://localhost:5000/receive";

            URL url = new URL(voiceUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true); // 출력 스트림 사용
            connection.setRequestProperty("Content-Type", "application/json"); // JSON 형식 지정

            // 파일 압축
            byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));

            // 바이트 배열을 전송
            connection.getOutputStream().write(fileBytes);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 200
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                System.out.println("Python server response: " + response.toString());


                // json 응답 파싱
                JsonNode jsonResponse = objectMapper.readTree(response.toString());
                int totalVoiceScore = jsonResponse.get("voice_score").asInt();
                System.out.println("total voice score: " + totalVoiceScore);

                // +) db 저장
                // File: video_path
                // Result: total_voice_score
                // Voice_result(double): voice_level, voice_speed, voice_intj, voice_score

                connection.disconnect();
                return totalVoiceScore;
            } else {
                System.out.println("Python server request failed with response code: " + responseCode);
                return -1;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // 행동 처리
    public int sendToAction(String filePath) {
        try {
            String videoUrl = "http://localhost:3000/receive";

            URL url = new URL(videoUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true); // 출력 스트림 사용

            // 파일 압축
            byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));

            // 바이트 배열을 전송
            connection.getOutputStream().write(fileBytes);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 200
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                System.out.println("nodejs server response: " + response.toString());


                // json 응답 파싱
                JsonNode jsonResponse = objectMapper.readTree(response.toString());
                int totalActionScore = jsonResponse.get("action_score").asInt();
                System.out.println("total action score: " + totalActionScore);


                // +) db 저장
                // File(char): video_path
                // Result(int): total_action_score
                // Action_result(double): face_gesture, eyetrack_gesture, body_gesture, hand_count_score, emotion_score, action_score

                connection.disconnect();
                return totalActionScore;
            } else {
                System.out.println("Python server request failed with response code: " + responseCode);
                return -1;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
