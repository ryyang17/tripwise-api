package com.example.tripwise_api.dto.response;

public class AuthResponse {

    private String accessToken;
    private String username;
    private String message;

    public AuthResponse() {}

    public AuthResponse(String accessToken, String username, String message) {
        this.accessToken = accessToken;
        this.username = username;
        this.message = message;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    // Legacy getter for backward compatibility
    public String getToken() {
        return accessToken;
    }

    public void setToken(String token) {
        this.accessToken = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
