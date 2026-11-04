package com.example.tripwise_api.dto.response;

@SuppressWarnings("unused")
public class AuthResponse {

    private String accessToken;
    private String username;
    private String message;
    private Long userId;


    public AuthResponse(String accessToken, String username, String message) {
        this(accessToken, username, message, null);
    }

    public AuthResponse(String accessToken, String username, String message, Long userId) {
        this.accessToken = accessToken;
        this.username = username;
        this.message = message;
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
