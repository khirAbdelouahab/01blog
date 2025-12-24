package com.blogger._blog.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.blogger._blog.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
    
    @Query("SELECT DISTINCT n FROM Notification n WHERE n.reciever.username = ?1 ORDER BY n.id DESC")
    public List<Notification> findAllByUserName(String username);

    @Query("SELECT count(n) FROM Notification n WHERE n.reciever.username = :username AND n.isRead = false")
    public Long UnreadNotificationsCount(@Param("username") String username);
}
