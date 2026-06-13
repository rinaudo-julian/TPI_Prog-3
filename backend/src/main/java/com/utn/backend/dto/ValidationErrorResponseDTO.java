package com.utn.backend.dto;

import java.time.LocalDateTime;

public record ValidationErrorResponseDTO(
        int status,
        String message,
        LocalDateTime timestamp,
        String[] errors
) {
}
