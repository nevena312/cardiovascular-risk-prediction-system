package com.cardiovascular.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateAssessmentRequest(
        @NotNull @Min(0) @Max(1) Integer sex,
        @NotNull @Min(18) @Max(110) Integer age,
        @NotNull @Min(0) @Max(1) Integer hyperlipidemia,
        @NotNull @Min(0) @Max(1) Integer smoker,
        @NotNull @Min(0) @Max(1) Integer diabetes,
        @NotNull @Min(0) @Max(1) Integer obesity,
        @NotNull @Min(0) @Max(1) Integer hypertension
) {
}
