package com.trocap.danhgiaai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAnalysisResult {
    private String ocrText;
    private Double ocrConfidence;
    private Double tamperingScore;
    private Boolean isTampered;
}
