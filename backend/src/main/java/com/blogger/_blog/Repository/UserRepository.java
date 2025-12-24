package com.blogger._blog.Repository;

import com.blogger._blog.details.OtherUserData;
import com.blogger._blog.details.SuggestionUserCard;
import com.blogger._blog.model.User;

import jakarta.persistence.Tuple;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.username = ?1")
    Optional<User> findByUsername(String username);
    @Query("SELECT u FROM User u WHERE u.email = ?1")
    User findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    @Query("SELECT new com.blogger._blog.details.OtherUserData(u.id,u.username,u.fullname, "+
    "(SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Subscribe s WHERE s.sender.username = ?1 AND s.receiver.id = u.id ), " +
    "(SELECT COUNT(s) FROM Subscribe s WHERE s.receiver.id = u.id), " +
    "(SELECT COUNT(s) FROM Subscribe s WHERE s.sender.id = u.id)) " +
    "FROM User u WHERE u.username != ?1")
    List<OtherUserData> findOthers(String username);
    @Query("SELECT u FROM User u WHERE u.role = 1")
    List<User> findAllUsers();
    @Query("SELECT u FROM User u WHERE u.role = 1 AND (LOWER(u.username) LIKE LOWER(CONCAT('%', :content, '%')) OR LOWER(u.fullname) LIKE LOWER(CONCAT('%', :content, '%')))")
    List<User> findAllUsers(String content);
    @Query("SELECT u as user, (SELECT count(n) FROM Notification n WHERE n.reciever.username = :username AND n.isRead = false) as notificationsCount FROM User u WHERE u.username = :username")
    public Tuple getUserDetails(@Param("username") String username);


}
