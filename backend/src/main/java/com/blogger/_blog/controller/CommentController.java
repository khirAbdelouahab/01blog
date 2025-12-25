package com.blogger._blog.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blogger._blog.details.CommentData;
import com.blogger._blog.details.CommentDataResponse;
import com.blogger._blog.details.Response;
import com.blogger._blog.model.Comment;
import com.blogger._blog.model.Post;
import com.blogger._blog.model.User;
import com.blogger._blog.service.CommentService;
import com.blogger._blog.service.PostService;
import com.blogger._blog.service.UserAuthenticationService;

@RestController
@RequestMapping("/api/comment")
@CrossOrigin(origins = "http://localhost:4200")
public class CommentController {
    @Autowired
    private UserAuthenticationService uService;
    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;

    @PostMapping("/new")
    public ResponseEntity<?> create(@RequestBody CommentData data, Authentication authentication) {
        if (data == null || data.getContent().equals("")) {
            return ResponseEntity.badRequest().body(new Response(false, "invalid data"));
        }
        User user = this.uService.findByUsername(authentication.getName());
        Post post = this.postService.getById(data.getPostId());
        if (post == null || user == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            Comment comment =  this.commentService.createComment(data.getContent(), post, user);
            CommentDataResponse commentConverted = CommentDataResponse.convert(comment, authentication.getName()); 
            return ResponseEntity.ok().body(commentConverted);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(new Response(false, "comment 'Content' is too long"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new Response(false, "InternalServer Error"));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> delete(@PathVariable("id") Long id, Authentication authentication) {
        String username = authentication.getName();
        boolean isFound = this.commentService.CommentExists(id);
        boolean isOwner = this.commentService.ExistsByCommnetIdAndAutherName(username, id);
        if (!isFound) {
            return ResponseEntity.notFound().build();
        }
        if (!isOwner) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Response(false, "you are not able to delete this comment"));
        }
        this.commentService.deleteComment(id);
        return ResponseEntity.ok().body(new Response(true, "comment deleted succesfuly"));
    }
}
