package com.eldercare.modules.auth.dto.response;

public class LoginResponse {
    private String token;
    private String sessionId;
    private String role;
    private String name;

    public LoginResponse(String token, String sessionId, String role, String name) {
        this.token = token;
        this.sessionId = sessionId;
        this.role = role;
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getRole() {
        return role;
    }

    public String getName() {
        return name;
    }
}
