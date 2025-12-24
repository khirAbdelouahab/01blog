package com.blogger._blog.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.blogger._blog.details.NotificationData;
import com.blogger._blog.details.Response;
import com.blogger._blog.model.Notification;
import com.blogger._blog.service.NotificationService;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/me/all")
    public ResponseEntity<List<NotificationData>> getNotificationsByUser(Authentication authentication) {
        String username = authentication.getName();
        List<NotificationData> list = this.notificationService.getAllNotificationsByUser(username);
        if (list == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/me/unread/count")
    public ResponseEntity<Integer> unReadNotificationsCount(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(0);
        }
        String username = authentication.getName();
        Integer count = Integer.valueOf(this.notificationService.unReadNotificationsCount(username).intValue());
        return ResponseEntity.ok().body(count);
    }

    @PostMapping("/asread/{id}")
    public ResponseEntity<Response> markAsRead(@PathVariable("id") Long id, Authentication authentication) {
        System.out.println("notification id: " + id);
        Notification notification = this.notificationService.findById(id);
        if (notification == null) {
            return ResponseEntity.notFound().build();
        }
        String username = authentication.getName();
        if (!notification.getReciever().getUsername().equals(username)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        this.notificationService.MarkAsRead(notification);
        return ResponseEntity.ok().body(new Response(true, "notification marked as read successfuly"));
    }

    @PostMapping("/asread/all")
    public ResponseEntity<Response> markAllAsRead( Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = authentication.getName();
        this.notificationService.MarkAllAsRead(username);
        return ResponseEntity.ok().body(new Response(true, "All Notifications Marked As Read Successfuly"));
    }
}
