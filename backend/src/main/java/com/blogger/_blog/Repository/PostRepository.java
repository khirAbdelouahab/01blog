package com.blogger._blog.Repository;
import com.blogger._blog.model.Post;
import com.blogger._blog.model.User;

import jakarta.persistence.Tuple;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.data.repository.query.Param;
@RepositoryDefinition(domainClass = Post.class, idClass = Long.class)
public interface PostRepository /*extends JpaRepository<Post, Long>*/ {

    Post save(Post post);
    List<Post> findAll();
    void delete(Post post);
    Optional<Post> getById(Long id);
    Optional<Post> findById(Long id);
    @Query("SELECT p FROM Post p WHERE p.author = ?1")
    public List<Post> findByAuthor(User author);
    

    @Query("SELECT DISTINCT p FROM Post p WHERE p.author.role=1 ORDER BY p.id DESC")
    List<Post> findAllUsersPosts();
    @Query("SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.mediaUploads WHERE p.author.id = ?1 ORDER BY p.id DESC")
    public List<Post> findByAuthor(Long author);

    @Query("SELECT DISTINCT p FROM Post p WHERE p.author.username = ?1 ORDER BY p.id DESC")
    public List<Post> findByAuthor(String authorName);

    @Query("SELECT p FROM Post p WHERE p.state != 1 AND (p.author.username =?1 OR p.author.id IN (SELECT  s.receiver.id FROM Subscribe s WHERE s.sender.username = ?1)) ORDER BY p.id DESC")
    public List<Post> findPostsBySubscribers(String username);

    @Query("SELECT p as post, (SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reaction r WHERE r.post.id=p.id AND r.author.username=:username) as isLikedByMe FROM Post p WHERE p.id = :postID")
    public Tuple findById(@Param("username") String username,@Param("postID") Long postID);
    
}
