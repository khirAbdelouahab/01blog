package com.blogger._blog.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.data.repository.query.Param;

import com.blogger._blog.model.Comment;


@RepositoryDefinition(domainClass = Comment.class, idClass = Long.class)
public interface CommentRepository /*extends JpaRepository<Comment,Long>*/ {
    Comment save(Comment comment);
    void delete(Comment comment);
    Optional<Comment> findById(Long id);
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Comment c WHERE c.id=:commentID AND c.author.username=:username")
    boolean ExistsByCommnetIdAndAutherName(@Param("username") String username, @Param("commentID") Long commentID);
}
