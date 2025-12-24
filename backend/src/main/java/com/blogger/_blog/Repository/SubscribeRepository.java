package com.blogger._blog.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.blogger._blog.details.ProfileFollowStats;
import com.blogger._blog.model.Subscribe;

public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {
    @Query("SELECT s FROM Subscribe s WHERE s.sender.id = :followerId and s.receiver.id = :followingId")
    public Subscribe findBySenderAndReciever(@Param("followerId") Long user1, @Param("followingId") Long user2);

    @Query("SELECT s FROM Subscribe s WHERE s.receiver.username = :username")
    public List<Subscribe> findSubscribersByUser(@Param("username") String username);

    @Query("SELECT new com.blogger._blog.details.ProfileFollowStats(" +
            "(SELECT COUNT(s) FROM Subscribe s WHERE s.receiver.username = :username), " +
            "(SELECT COUNT(s) FROM Subscribe s WHERE s.sender.username = :username)) " +
            "FROM Subscribe s GROUP BY 1")
    public ProfileFollowStats getUserState(@Param("username") String username);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Subscribe s WHERE s.receiver.username = :receivername AND s.sender.username = :sendername")
    public boolean isSubscribedByMe(@Param("sendername") String sendername, @Param("receivername") String receivername);
    
}
