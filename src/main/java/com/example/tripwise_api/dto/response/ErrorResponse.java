package com.example.tripwise_api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class ErrorResponse {
    private String message;
    private Object details;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    private Integer status;
    private String path;

    // Constructors
    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(String message) {
        this();
        this.message = message;
    }

    public ErrorResponse(String message, Object details) {
        this();
        this.message = message;
        this.details = details;
    }

    public ErrorResponse(String message, Object details, Integer status, String path) {
        this();
        this.message = message;
        this.details = details;
        this.status = status;
        this.path = path;
    }

    // Getters and Setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Object getDetails() { return details; }
    public void setDetails(Object details) { this.details = details; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
}

