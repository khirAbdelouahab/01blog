package com.blogger._blog.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import com.blogger._blog.Repository.CommentRepository;
import com.blogger._blog.model.Comment;
import com.blogger._blog.model.Post;
import com.blogger._blog.model.User;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    public void createComment(String content,Post post,User user) throws DataIntegrityViolationException { 
        try {
            Comment comment = new Comment(content, post, user);
            this.commentRepository.save(comment);
        } catch (DataIntegrityViolationException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    public void deleteComment(Long id) { 
        Comment comment = this.commentRepository.findById(id).orElse(null);
        if (comment != null) {
            this.commentRepository.delete(comment);
        }
    }

    public boolean ExistsByCommnetIdAndAutherName(String username, Long commentId) {
        return this.commentRepository.ExistsByCommnetIdAndAutherName(username, commentId);
    }

    public boolean CommentExists(Long id) {
        Comment comment = this.commentRepository.findById(id).orElse(null);
        return comment == null ? false:true;
    }
}
