package com.blogger._blog.Repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.blogger._blog.model.MediaUpload;
import com.blogger._blog.model.Post;

public interface MediaUploadRepository extends JpaRepository<MediaUpload, Long> {
    @Query("SELECT m FROM MediaUpload m WHERE m.post = ?1")
    public List<MediaUpload> findByPost(Post post);
    //SELECT p FROM Post p JOIN FETCH p.author WHERE p.author.id = ?1
    @Query("SELECT m FROM MediaUpload m JOIN FETCH m.post WHERE m.post.id = ?1")
    public List<MediaUpload> findByPostID(Long post);

    @Query("SELECT m FROM MediaUpload m WHERE m.id = ?1")
    public List<MediaUpload> findID(Long mediaUpload);
}
