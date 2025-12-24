package com.blogger._blog.controller;

import com.blogger._blog.CustomExceptions.FileSizeLimitExceededException;
import com.blogger._blog.details.MediaUploadDataResponse;
import com.blogger._blog.details.PostDataResponse;
import com.blogger._blog.details.PostDataResponseView;
import com.blogger._blog.details.Response;
import com.blogger._blog.enums.PostState;
import com.blogger._blog.enums.UserState;
import com.blogger._blog.model.Post;
import com.blogger._blog.model.Reaction;
import com.blogger._blog.model.User;
import com.blogger._blog.service.MediaUploadService;
import com.blogger._blog.service.PostService;
import com.blogger._blog.service.ReactionService;
import com.blogger._blog.service.UserAuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class PostController {
    @Autowired
    private PostService postService;
    @Autowired
    private UserAuthenticationService userService;
    @Autowired
    private MediaUploadService mediaUploadService;
    @Autowired
    private ReactionService reactionService;

    @GetMapping("/posts")
    public ResponseEntity<List<PostDataResponse>> getAllPosts(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }
        String username = authentication.getName();
        List<PostDataResponse> posts = this.postService.getThemAll(username);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/posts/{username}")
    public ResponseEntity<List<PostDataResponse>> getAllPosts(@PathVariable("username") String username) {

        return ResponseEntity.ok(this.postService.getThemByUser(username));
    }

    @GetMapping("/posts/post/view/{id}")
    public ResponseEntity<PostDataResponseView> getPostView(@PathVariable("id") Long id,
            Authentication authentication) {
        String username = authentication.getName();
        PostDataResponseView p = this.postService.getById(username, id);
        if (p == null) {
            return ResponseEntity.notFound().build();
        }

        if (p.getPost().getState().equals(PostState.HIDDEN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (p.getPost().getAuthor().getUsername().equals(username)) {
            p.getPost().setCreatedByMe(true);
        }

        return ResponseEntity.ok(p);
    }

    @GetMapping("/posts/post/{id}")
    public ResponseEntity<PostDataResponse> getPost(@PathVariable("id") Long id, Authentication authentication) {
        Post p = this.postService.getById(id);
        if (p == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(PostDataResponse.convert(p));
    }

    @GetMapping("/posts/myPosts")
    public ResponseEntity<List<PostDataResponse>> getAllMyPosts(Authentication authentication) {
        String userName = authentication.getName();
        return ResponseEntity.ok(this.postService.getThemByUser(userName));
    }

    @GetMapping("/test")
    public ResponseEntity<Response> getTest(Authentication authentication) {
        String userName = authentication.getName();
        return ResponseEntity.ok(new Response(true, "user is " + userName));
    }

    @PostMapping("/posts/{id}/like")
    public ResponseEntity<Response> LikePost(@PathVariable("id") Long id, Authentication authentication) {
        User user = this.userService.findByUsername(authentication.getName());
        Post post = this.postService.getById(id);
        if (user == null || post == null) {
            return ResponseEntity.notFound().build();
        }
        if (user.getState().equals(UserState.banned) || post.getState().equals(PostState.HIDDEN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response(true, "user is banned Or post is hidden"));

        }
        Reaction reaction = this.reactionService.get(user.getId(), post.getId());
        if (reaction == null) {
            this.reactionService.add(user, post);
        } else {
            this.reactionService.remove(user, post);
        }
        Long reactionCounts = this.reactionService.countsByPostId(post.getId());
        return ResponseEntity.ok(new Response(true, "succes", reactionCounts));
    }

    @PostMapping("/posts/new")
    @Transactional
    public ResponseEntity<Response> createPost(
            @RequestParam(value = "files", required = false) MultipartFile[] files,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "mediaContents", required = false) String mediaContents,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            Authentication authentication) {
        Post post = new Post(title, content, category);
        User user = userService.findByUsername(authentication.getName());
        post.setAuthor(user);
        try {
            Post createdPost = this.postService.createPost(post);
            if (createdPost == null) {
                return ResponseEntity.notFound().build();
            }
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(new Response(false, "content or title data are too long"));
        }
        try {
            String[] mediaContentsArray = new ObjectMapper().readValue(mediaContents, String[].class);
            if (files != null) {
                this.mediaUploadService.Create(post, files, mediaContentsArray);
            }
            return ResponseEntity.ok(
                    new Response(true, "Post created successfully"));

        } catch (FileSizeLimitExceededException e) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body(new Response(false, e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(new Response(false, e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(500)
                    .body(new Response(false, e.getMessage()));
        }
    }

    @PostMapping("/posts/update")
    public ResponseEntity<Response> updatePost(
            @RequestParam(value = "files", required = false) MultipartFile[] files,
            @RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "mediaJson", required = false) String mediaJson,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            Authentication authentication) {
        String username = authentication.getName();
        Long postId = Long.valueOf(id);
        Post post = this.postService.getById(postId);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }
        if (!post.getAuthor().getUsername().equals(username)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new Response(false, "you can't!. because this post is not created by you"));
        }
        post.setCategory(category);
        post.setContent(content);
        post.setTitle(title);

        try {
            Post updatedPost = this.postService.updatePost(post);
            if (updatedPost == null) {
                return ResponseEntity.badRequest().body(new Response(false, "something happend wrong"));
            }
            if (mediaJson != null) {
                ObjectMapper mapper2 = new ObjectMapper();
                List<MediaUploadDataResponse> media = Arrays
                        .asList(mapper2.readValue(mediaJson, MediaUploadDataResponse[].class));
                List<MediaUploadDataResponse> updatedMedia = media.stream().filter(m -> !m.getId().equals(-1)).toList();
                this.mediaUploadService.generatePostMedia(postId, updatedMedia);
                String[] MediaContents = new String[media.size()];
                int index = 0;
                for (MediaUploadDataResponse m : media) {
                    if (m.getId() == -1) {
                        MediaContents[index] = m.getContent();
                        index++;
                    }
                }
                if (files != null) {
                    this.mediaUploadService.Create(post, files, MediaContents);
                }
                return ResponseEntity.ok(new Response(true, "Post updated successfully"));
            }
            return ResponseEntity.ok(
                    new Response(true, "Post updated successfully"));
        } catch (FileSizeLimitExceededException e) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body(new Response(false, e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(new Response(false, "Some data is too long"));
        } catch (IOException e) {
            return ResponseEntity.status(500)
                    .body(new Response(false, e.getMessage()));
        }

    }

    @DeleteMapping("/posts/delete/{id}")
    public ResponseEntity<Response> deletePost(@PathVariable("id") Long id, Authentication authentication) {
        Post p = this.postService.getById(id);
        String username = authentication.getName();
        if (p == null) {
            return ResponseEntity.notFound().build();
        }
        if (p.getAuthor().getUsername().equals(username)) {
            // this.mediaUploadService.deletePostMedia(p);
            this.postService.deletePost(p);
            return ResponseEntity.ok().body(new Response(true, "post deleted succesfuly"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Response(false, "not authorized"));
        }
    }

}
