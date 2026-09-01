package com.cardiovascular.dto;

import java.time.LocalDateTime;

public record PatientResponse(
        Long id,
        String patientCode,
        String firstName,
        String lastName,
        LocalDateTime createdAt,
        AssessmentSummaryResponse latestAssessment
) {
}
