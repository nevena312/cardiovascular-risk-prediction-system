package com.cardiovascular.dto;

import java.time.LocalDateTime;

public record AssessmentSummaryResponse(
        Long id,
        Long patientId,
        String patientCode,
        String patientName,
        int sex,
        int age,
        int hyperlipidemia,
        int smoker,
        int diabetes,
        int obesity,
        int hypertension,
        double predictedProbability,
        int predictedClass,
        LocalDateTime createdAt
) {
}
