package com.blogger._blog.Repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.blogger._blog.model.Reaction;
import jakarta.transaction.Transactional;

public interface ReactionRepository extends JpaRepository<Reaction,Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Reaction r WHERE r.post.id = :postId and r.author.id= :userId")
    public void deleteByPostandUserIds(@Param("postId") Long postId,@Param("userId") Long userId);
    @Query("SELECT r FROM Reaction r WHERE r.post.id = :postId and r.author.id= :userId")
    public Optional<Reaction> findByPostAndUsersIds(@Param("postId") Long postId,@Param("userId") Long userId);
    @Query("SELECT count(r) FROM Reaction r WHERE r.post.id = :postId")
    public Long countByPostId(@Param("postId") Long postId);
}
