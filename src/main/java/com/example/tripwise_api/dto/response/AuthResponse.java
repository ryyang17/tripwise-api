package com.example.tripwise_api.dto.response;

@SuppressWarnings("unused")
public class AuthResponse {

    private String accessToken;
    private String username;
    private String message;


    public AuthResponse(String accessToken, String username, String message) {
        this.accessToken = accessToken;
        this.username = username;
        this.message = message;
    }

    public String getAccessToken() {
        return accessToken;
    }

    @SuppressWarnings("unused")
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
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
