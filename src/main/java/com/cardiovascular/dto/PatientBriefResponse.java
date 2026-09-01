package com.cardiovascular.dto;

public record PatientBriefResponse(
        Long id,
        String patientCode,
        String firstName,
        String lastName
) {
}
