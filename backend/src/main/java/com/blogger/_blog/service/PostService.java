package com.blogger._blog.service;

import com.blogger._blog.Repository.PostRepository;
import com.blogger._blog.details.CommentDataResponse;
import com.blogger._blog.details.MediaUploadDataResponse;
import com.blogger._blog.details.PostDataResponse;
import com.blogger._blog.details.PostDataResponseView;
import com.blogger._blog.details.UserDataResponse;
import com.blogger._blog.enums.PostState;
import com.blogger._blog.model.Post;

import jakarta.persistence.Tuple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private NotificationService notificationService;

    public Post createPost(Post post) throws DataIntegrityViolationException {
        if (post.getAuthor() == null) {
            throw new IllegalArgumentException("Post author cannot be null");
        }

        // Add validation for title and content length
        if (post.getTitle() != null && post.getTitle().length() > 150) {
            throw new IllegalArgumentException("Title cannot exceed 150 characters");
        }

        if (post.getContent() != null && post.getContent().length() > 3000) {
            throw new IllegalArgumentException("Content cannot exceed 3000 characters");
        }

        post.setCreationDate(new Date());

        try {
            Post createdPost = this.postRepository.save(post);
            this.notificationService.notifySubscribers(createdPost);
            return createdPost;
        } catch (DataIntegrityViolationException e) {
            // Log the error and throw a more user-friendly exception
            throw new IllegalArgumentException("Failed to create post: " + e.getMostSpecificCause().getMessage());
        }
    }

    public Post updatePost(Post post) throws DataIntegrityViolationException {
        if (post.getAuthor() == null) {
            throw new IllegalArgumentException("Post author cannot be null");
        }

        // Add validation for title and content length
        if (post.getTitle() != null && post.getTitle().length() > 150) {
            throw new IllegalArgumentException("Title cannot exceed 150 characters");
        }

        if (post.getContent() != null && post.getContent().length() > 3000) {
            throw new IllegalArgumentException("Content cannot exceed 3000 characters");
        }
        try {
            Post updatedPost = this.postRepository.save(post);
            return updatedPost;
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Failed to create post: " + e.getMostSpecificCause().getMessage());
        }

    }

    public List<Post> getAllUsersPosts() {
        List<Post> posts = this.postRepository.findAllUsersPosts();
        return posts;
    }

    public List<PostDataResponse> getThemAll() {
        return this.convert(postRepository.findAll());
    }

    public List<PostDataResponse> getThemAll(String username) {
        return this.convert(this.postRepository.findPostsBySubscribers(username), username);
    }

    public List<PostDataResponse> getThemByUser(Long author) {
        return this.convert(postRepository.findByAuthor(author));
    }

    public List<PostDataResponse> getThemByUser(String authorName) {
        return this.convert(this.postRepository.findByAuthor(authorName));
    }

    public Post getById(Long id) {
        Post post = this.postRepository.findById(id).orElse(null);
        if (post == null) {
            return null;
        }
        return post;
    }

    public PostDataResponseView getById(String username, Long id) {
        Tuple data = this.postRepository.findById(username, id);
        if (data == null) {
            return null;
        }
        Post post = data.get("post", Post.class);
        boolean isLikedByMe = data.get("isLikedByMe", boolean.class);
        if (post == null) {
            return null;
        }
        PostDataResponseView p = new PostDataResponseView(PostDataResponse.convert(post, username), isLikedByMe);
        return p;
    }

    public static PostDataResponse convertPost(Post p) {
        if (p == null) {
            return null;
        }
        PostDataResponse post = new PostDataResponse(p.getId(), p.getTitle(), p.getContent(), p.getCategory(),
                p.getCreationDate());
        post.setState(p.getState());
        return post;
    }

    public PostDataResponse convert(Post p) {
        PostDataResponse post = new PostDataResponse(p.getId(), p.getTitle(), p.getContent(), p.getCategory(),
                p.getCreationDate());
        UserDataResponse user = UserDataResponse.convert(p.getAuthor());
        post.setComments(CommentDataResponse.convert(p.getComments()));
        post.setState(p.getState());
        post.setLikes(p.getReactions().size());
        post.setMediaUploads(MediaUploadDataResponse.convert(p.getMediaUploads()));
        return post.setAuthor(user);
    }

    public List<PostDataResponse> convert(List<Post> posts) {
        List<PostDataResponse> allPosts = new ArrayList<>();
        for (int i = 0; i < posts.size(); i++) {
            PostDataResponse p = this.convert(posts.get(i));
            allPosts.add(p);
        }
        return allPosts;
    }

    public List<PostDataResponse> convert(List<Post> posts, String connectedUserName) {
        List<PostDataResponse> allPosts = new ArrayList<>();
        for (int i = 0; i < posts.size(); i++) {
            PostDataResponse p = this.convert(posts.get(i));
            if (p.getAuthor().getUsername().equals(connectedUserName)) {
                p.setCreatedByMe(true);
            }
            allPosts.add(p);
        }
        return allPosts;
    }

    public void UpdateAllPostsState(PostState newState) {
        List<Post> posts = this.postRepository.findAll();
        for (int index = 0; index < posts.size(); index++) {
            Post p = posts.get(index);
            p.setState(newState);
            this.postRepository.save(p);
        }
    }

    public void updatePostState(Post post, PostState newState) {
        if (post != null) {
            post.setState(newState);
            this.postRepository.save(post);
        }
    }

    public void deletePost(Post post) {
        this.postRepository.delete(post);
    }
}
