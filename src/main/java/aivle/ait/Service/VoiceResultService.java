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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoiceResultService {
    private final ObjectMapper objectMapper;
    private final VoiceResultRepository voiceResultRepository;
    private final FileRepository fileRepository;

    @Value("${ait.server.voiceServer}")
    private String baseUrl;

    // 음성
    @Async
    @Transactional
    public CompletableFuture<String> sendToVoice(FileDTO fileDTO) {
        try {
            String filePath = fileDTO.getVideo_path();

            String voiceUrl = baseUrl + "/voice";

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
                return null;
            }
            VoiceResult voiceResult = new VoiceResult();
            voiceResult.setVoice_level(jsonResponse.get("voice_level").asDouble());
            voiceResult.setVoice_speed(jsonResponse.get("voice_speed").asDouble());
            voiceResult.setVoice_intj(jsonResponse.get("voice_intj").asDouble());
            voiceResult.setVoice_score(totalVoiceScore);
            voiceResult.setFile(file.get());

            voiceResultRepository.save(voiceResult);

            String interviewerAnswer = jsonResponse.get("inference").asText();
            return CompletableFuture.completedFuture(interviewerAnswer);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
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
