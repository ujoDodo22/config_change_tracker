package org.example.config_change_tracker.dto;

import java.time.Instant;

public class ErrorResponse {

    private final Instant timestamp;
    private final int status;
    private final String error;

    public ErrorResponse(Instant timestamp, int status, String error) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
    }

    public Instant getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
}
