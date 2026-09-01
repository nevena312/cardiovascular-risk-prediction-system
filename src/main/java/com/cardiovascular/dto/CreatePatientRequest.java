package com.cardiovascular.dto;

import jakarta.validation.constraints.Size;

public record CreatePatientRequest(
        @Size(max = 32) String patientCode,
        @Size(max = 100) String firstName,
        @Size(max = 100) String lastName
) {
}
