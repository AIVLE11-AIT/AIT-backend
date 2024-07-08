package aivle.ait.Service;

import aivle.ait.Dto.FileDTO;
import aivle.ait.Dto.VoiceResultDTO;
import aivle.ait.Entity.File;
import aivle.ait.Entity.VoiceResult;
import aivle.ait.Repository.FileRepository;
import aivle.ait.Repository.VoiceResultRepository;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoiceResultService {
    private final ObjectMapper objectMapper;
    private final VoiceResultRepository voiceResultRepository;
    private final FileRepository fileRepository;

    // 음성
    @Async
    @Transactional
    public void sendToVoice(FileDTO fileDTO) {
        try {
            String filePath = fileDTO.getVideo_path();

            String voiceUrl = "http://192.168.0.6:5000/voice";

            String response = RestAPIUtil.sendPostFile(voiceUrl, filePath, "file");

            // json 응답 파싱
            JsonNode jsonResponse = objectMapper.readTree(response);
            double totalVoiceScore = jsonResponse.get("voice_score").asDouble();
            System.out.println("total voice score: " + totalVoiceScore);

            // +) db 저장
            // File: video_path
            // Result: total_voice_score
            // Voice_result(double): voice_level, voice_speed, voice_intj, voice_score
            Optional<File> file = fileRepository.findById(fileDTO.getId());
            if (file.isEmpty()) {
                return;
            }
            VoiceResult voiceResult = new VoiceResult();
            voiceResult.setVoice_level(jsonResponse.get("voice_level").asDouble());
            voiceResult.setVoice_speed(jsonResponse.get("voice_speed").asDouble());
            voiceResult.setVoice_intj(jsonResponse.get("voice_intj").asDouble());
            voiceResult.setVoice_score(totalVoiceScore);
            voiceResult.setFile(file.get());

            voiceResultRepository.save(voiceResult);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public VoiceResultDTO readOne(Long id){
        Optional<VoiceResult> voiceResult = voiceResultRepository.findById(id);
        if (voiceResult.isEmpty()){
            return null;
        }
        VoiceResultDTO voiceResultDTO = new VoiceResultDTO(voiceResult.get());
        return voiceResultDTO;
    }
}
