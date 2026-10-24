package ru.practicum.ewm.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ErrorResponse {
    private final String reason;
    private final String message;
    private final String status;
    private final String timestamp;

    public ErrorResponse(String reason, String message, String status) {
        this.reason = reason;
        this.message = message;
        this.status = status;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.timestamp = LocalDateTime.now().format(formatter);
    }

    public String getReason() {
        return reason;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    public String getTimestamp() {
        return timestamp;
    }
}