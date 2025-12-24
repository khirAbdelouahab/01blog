package com.blogger._blog.service;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.blogger._blog.Repository.NotificationRepository;
import com.blogger._blog.details.NotificationData;
import com.blogger._blog.model.Notification;
import com.blogger._blog.model.Post;
import com.blogger._blog.model.User;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private FollowingService followingService;
    public List<NotificationData> getAllNotificationsByUser(String username) {
        List<Notification> notifications = this.notificationRepository.findAllByUserName(username);
        List<NotificationData> notificationsDataConverted = NotificationData.convert(notifications);
        return notificationsDataConverted;
    }
    public Notification createNotification(User reciever, Post post,String content) {
        if (reciever == null || post == null) {
            return null;
        }
        Notification notification = new Notification(post, reciever, content);
        return this.notificationRepository.save(notification);
    }
    public void notifySubscribers(Post post) {
        List<User> subscribers = this.followingService.getAllSubscribersByUser(post.getAuthor().getUsername());
        for (int i = 0; i < subscribers.size(); i++) {
            User user = subscribers.get(i);
            if (user != null) {
                String postContent = post.getContent();
                if (postContent.length() > 150 ) {
                    postContent = postContent.substring(0, 150) + "...";
                }
                String content = String.format("%s posted : %s", post.getAuthor().getFullname(), postContent); 
                this.createNotification(user, post, content);
            }
        }
    }
    public Long unReadNotificationsCount(String username) {
        return this.notificationRepository.UnreadNotificationsCount(username);
    }

    public void MarkAsRead(Notification notification) {
        if (notification != null) {
            notification.setRead(true);
            this.notificationRepository.save(notification);            
        }
    }

    public void MarkAllAsRead(String username) {
        List<Notification> notifications = this.notificationRepository.findAllByUserName(username);
        for (int i = 0; i < notifications.size(); i++) {
            Notification notification = notifications.get(i);
            this.MarkAsRead(notification);
        }
    }


    public Notification findById(Long id) {
        return this.notificationRepository.findById(id).orElse(null);
    }

}
