package com.cardiovascular.dto;

import java.time.LocalDateTime;
import java.util.List;

public record AssessmentResponse(
        Long id,
        PatientBriefResponse patient,
        int sex,
        int age,
        int hyperlipidemia,
        int smoker,
        int diabetes,
        int obesity,
        int hypertension,
        double predictedProbability,
        int predictedClass,
        LocalDateTime createdAt,
        List<FeatureContributionResponse> contributions
) {
}
