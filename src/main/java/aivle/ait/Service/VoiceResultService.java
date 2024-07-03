package aivle.ait.Service;

import aivle.ait.Dto.FileDTO;
import aivle.ait.Dto.VoiceResultDTO;
import aivle.ait.Entity.File;
import aivle.ait.Entity.VoiceResult;
import aivle.ait.Repository.FileRepository;
import aivle.ait.Repository.VoiceResultRepository;
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
            String boundary = "*****"; // 바운더리 문자열 설정

            String voiceUrl = "http://192.168.0.6:5000/voice";

            URL url = new URL(voiceUrl);
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
            // 데이터 출력 스트림 생성
            DataOutputStream request = new DataOutputStream(connection.getOutputStream());

            // 멀티파트 폼 데이터 구성
            request.writeBytes("--" + boundary + "\r\n");
            request.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + fileName + "\"" + "\r\n");
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
                System.out.println("Python server response: " + response.toString());


                // json 응답 파싱
                JsonNode jsonResponse = objectMapper.readTree(response.toString());
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
                VoiceResultDTO voiceResultDTO = new VoiceResultDTO(voiceResult);

                connection.disconnect();
                return;
            } else {
                System.out.println("Python server request failed with response code: " + responseCode);
            }
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
