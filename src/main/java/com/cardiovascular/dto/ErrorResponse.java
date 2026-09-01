package com.cardiovascular.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        List<String> details
) {
}
