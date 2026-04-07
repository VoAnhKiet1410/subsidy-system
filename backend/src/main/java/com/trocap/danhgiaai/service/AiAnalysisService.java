package com.trocap.danhgiaai.service;

import com.trocap.danhgiaai.dto.AiAnalysisResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@Service
@Slf4j
public class AiAnalysisService {

    private final RestTemplate restTemplate = new RestTemplate();
    // Container Python sẽ chạy ở cổng 8000 này:
    private final String AI_SERVICE_URL = "http://localhost:8000/api/v1/analyze-document";

    public AiAnalysisResult analyzeDocument(MultipartFile file) {
        try {
            return analyzeDocumentBytes(file.getBytes(), file.getOriginalFilename());
        } catch (Exception e) {
            log.error("Lỗi đọc file: ", e);
            return null;
        }
    }

    public AiAnalysisResult analyzeDocumentBytes(byte[] fileBytes, String filename) {
        log.info("Đang gọi sang AI Service để lấy OCR cho file: {}", filename);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            
            // Ép file vào định dạng Mutilpart resource
            ByteArrayResource fileAsResource = new ByteArrayResource(fileBytes) {
                @Override
                public String getFilename() {
                    return filename != null ? filename : "document.png";
                }
            };
            body.add("file", fileAsResource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(AI_SERVICE_URL, requestEntity, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                
                log.info("AI Service phân tích xong!");
                return AiAnalysisResult.builder()
                        .ocrText((String) responseBody.get("ocr_text"))
                        .ocrConfidence((Double) responseBody.get("ocr_confidence"))
                        .tamperingScore((Double) responseBody.get("tampering_score"))
                        .isTampered((Boolean) responseBody.get("is_tampered"))
                        .build();
            }
        } catch (Exception e) {
            log.error("Lỗi khi kết nối tới Microservice AI Python: {}", e.getMessage());
        }
        return null; // Hoặc trả về Optional / AI rỗng nếu lỗi mạng
    }
}
