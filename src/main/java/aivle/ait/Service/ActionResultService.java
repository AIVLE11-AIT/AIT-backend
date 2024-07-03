package aivle.ait.Service;

import aivle.ait.Dto.ActionResultDTO;
import aivle.ait.Dto.FileDTO;
import aivle.ait.Entity.ActionResult;
import aivle.ait.Entity.File;
import aivle.ait.Repository.ActionResultRepository;
import aivle.ait.Repository.FileRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActionResultService {
    private final ObjectMapper objectMapper;
    private final ActionResultRepository actionResultRepository;
    private final FileRepository fileRepository;

    // 행동 처리
    @Async
    public void sendToAction(FileDTO fileDTO) {
        try {
            String filePath = fileDTO.getVideo_path();
            String videoUrl = "http://192.168.0.3:3000/process-video";
            String boundary = "*****"; // 바운더리 문자열 설정

            URL url = new URL(videoUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // HTTP 연결 설정
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            // 파일 압축
            byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
            String fileName = Paths.get(filePath).getFileName().toString();

            // 바이트 배열을 전송
            // 바이트 배열을 전송
            // 데이터 출력 스트림 생성
            DataOutputStream request = new DataOutputStream(connection.getOutputStream());

            // 멀티파트 폼 데이터 구성
            request.writeBytes("--" + boundary + "\r\n");
            request.writeBytes("Content-Disposition: form-data; name=\"video\";filename=\"" + fileName + "\"" + "\r\n");
            request.writeBytes("\r\n");

            // 파일 데이터를 바이트 배열로 전송
            request.write(fileBytes);

            // 멀티파트 폼 데이터 종료
            request.writeBytes("\r\n");
            request.writeBytes("--" + boundary + "--" + "\r\n");

            // 스트림 닫기
            request.flush();
            request.close();

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
                double totalActionScore = jsonResponse.get("action_score").asDouble();
                System.out.println("total action score: " + totalActionScore);


                // +) db 저장
                // File(char): video_path
                // Result(int): total_action_score
                // Action_result(double): face_gesture, eyetrack_gesture, body_gesture, hand_count_score, emotion_score, action_score
                Optional<File> file = fileRepository.findById(fileDTO.getId());
                if (file.isEmpty()) {
                    return;
                }
                ActionResult actionResult = new ActionResult();
                actionResult.setEmotion_score(jsonResponse.get("emotion_score").asDouble());
                actionResult.setFace_gesture_score(jsonResponse.get("face_gesture_score").asDouble());
                actionResult.setEyetrack_gesture_score(jsonResponse.get("eyetrack_gesture_score").asDouble());
                actionResult.setBody_gesture_score(jsonResponse.get("body_gesture_score").asDouble());
                actionResult.setHand_count_score(jsonResponse.get("hand_count_score").asDouble());
                actionResult.setAction_score(jsonResponse.get("action_score").asDouble());
                actionResult.setFile(file.get());

                actionResultRepository.save(actionResult);

                connection.disconnect();

            } else {
                System.out.println("Python server request failed with response code: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ActionResultDTO readOne(Long id){
        Optional<ActionResult> actionResults = actionResultRepository.findById(id);
        if (actionResults.isEmpty()){
            return null;
        }
        ActionResultDTO actionResultDTO = new ActionResultDTO(actionResults.get());

        return actionResultDTO;
    }
}
