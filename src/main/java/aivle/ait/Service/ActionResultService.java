package aivle.ait.Service;

import aivle.ait.Dto.ActionResultDTO;
import aivle.ait.Dto.FileDTO;
import aivle.ait.Entity.ActionResult;
import aivle.ait.Entity.File;
import aivle.ait.Repository.ActionResultRepository;
import aivle.ait.Repository.FileRepository;
import aivle.ait.Util.RestAPIUtil;
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

            String response = RestAPIUtil.sendPostFile(videoUrl, filePath, "video");

            // json 응답 파싱
            JsonNode jsonResponse = objectMapper.readTree(response);
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
