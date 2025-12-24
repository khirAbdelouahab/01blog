package com.blogger._blog.details;

public class UserData {
    private UserDataResponse user;
    private Long notificationsCount;

    public UserData() {}

    public UserData(UserDataResponse user,Long notificationsCount) {
        this.user=user;
        this.notificationsCount=notificationsCount;
    }

    public UserDataResponse getUser() {
        return this.user;
    }

    public Long getNotificationsCount() {
        return this.notificationsCount;
    }

}
