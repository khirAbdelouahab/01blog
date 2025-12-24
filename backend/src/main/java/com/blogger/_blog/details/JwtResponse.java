package com.blogger._blog.details;

public class JwtResponse {
    private boolean success;
    private String token;
    private UserDataResponse userConnected;
    private Long notificationsCount;

    public JwtResponse(String token,UserDataResponse userConnected,Long notificationsCount) {
        this.token = token;
        this.success = true;
        this.notificationsCount = notificationsCount;
        this.userConnected = userConnected;
    }

    public String getToken() { return token; }

    public void setToken(String token) { this.token = token; }

    public UserDataResponse getUserConnected() {
        return this.userConnected;
    }

    public Long getNotificationsCount() {
        return this.notificationsCount;
    }
    public void setUserConnected(UserDataResponse userConnected) {
        this.userConnected = userConnected;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
