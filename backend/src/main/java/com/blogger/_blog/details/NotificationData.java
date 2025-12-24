package com.blogger._blog.details;
import java.util.ArrayList;
import java.util.List;
import com.blogger._blog.model.Notification;

public class NotificationData {
    private Long id;
    private PostDataResponse post;
    private UserDataResponse reciever;
    private String content;
    private boolean read;
    NotificationData() {}
    NotificationData(Long id,PostDataResponse post,UserDataResponse reciever,String content,boolean read) {
        this.id = id;
        this.post=post;
        this.reciever=reciever;
        this.content=content;
        this.read = read;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public PostDataResponse getPost() {
        return this.post;
    }
    public void setPost(PostDataResponse post) {
        this.post = post;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public UserDataResponse getReciever() {
        return this.reciever;
    }
    public void setReciever(UserDataResponse reciever) {
        this.reciever = reciever;
    }
    public boolean getRead() {
        return this.read;
    }
    public void setRead(boolean read) {
        this.read = read;
    }
    public static NotificationData convert(Notification notification) {
        if (notification == null) {
            return null;
        }
        NotificationData nData = new NotificationData();
        nData.setId(notification.getId());
        nData.setContent(notification.getContent());
        nData.setPost(PostDataResponse.convert(notification.getPost()));
        nData.setReciever(UserDataResponse.convert(notification.getReciever()));
        nData.setRead(notification.getRead());
        return nData;
    }
    public static List<NotificationData> convert(List<Notification> notifications) {
        List<NotificationData> notificationsData = new ArrayList<>();
        for (int i = 0; i < notifications.size(); i++) {
            notificationsData.add(NotificationData.convert(notifications.get(i)));
        }
        return notificationsData;
    }
}
