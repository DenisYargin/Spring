package com.Yargin.reservation;

import java.time.LocalDateTime;

public record ErrorResponseDto(
        String message,
        String ErrorMessage,
        LocalDateTime erorrTime
) {
}
