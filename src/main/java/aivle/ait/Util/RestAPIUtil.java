package aivle.ait.Util;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;


public class RestAPIUtil {

    public static String sendPostJson(String url, MultiValueMap<String, Object> body){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 객체 생성
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        // request
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        String responseBody = response.getBody();

        return responseBody;
    }

    public static String sendPostFile(String strUrl, String filePath, String key) throws IOException {
        String boundary = "strUrl"; // 바운더리 문자열 설정
        URL url = new URL(strUrl);
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
        request.writeBytes("Content-Disposition: form-data; name=\"" + key + "\";filename=\"" + fileName + "\"" + "\r\n");
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

            return response.toString();
        } else {
            System.out.println("Python server request failed with response code: " + responseCode);
            return null;
        }
    }
}
