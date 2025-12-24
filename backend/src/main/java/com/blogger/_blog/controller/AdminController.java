package com.blogger._blog.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blogger._blog.details.PostDataResponse;
import com.blogger._blog.details.Response;
import com.blogger._blog.details.UserDataResponse;
import com.blogger._blog.enums.PostState;
import com.blogger._blog.model.Post;
import com.blogger._blog.model.User;
import com.blogger._blog.service.AdminService;
import com.blogger._blog.service.PostService;
import com.blogger._blog.service.UserAuthenticationService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private UserAuthenticationService uService;
    @Autowired
    private PostService postService;

    @GetMapping("/users")
    public ResponseEntity<List<UserDataResponse>> getAllUsers() {
        List<UserDataResponse> users = this.adminService.getAllUsers();
        if (users == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(users);
    }

    @GetMapping("/posts")
    public ResponseEntity<List<PostDataResponse>> getAllPosts() {
        List<PostDataResponse> posts = this.adminService.getAllPosts();
        if (posts == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(posts);
    }

    @DeleteMapping("/users/delete/{username}")
    public ResponseEntity<Response> deleteUser(@PathVariable String username, Authentication authentication) {
        User user = this.uService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        if (authentication == null) {
            return ResponseEntity.badRequest().body(new Response(false, "not authenticated"));
        }
        if (username.equals(authentication.getName())) {
            return ResponseEntity.badRequest().body(new Response(false, "you can't delete your self"));
        }
        this.uService.delete(user);
        return ResponseEntity.ok().body(new Response(true, "user deleted succesfuly"));
    }

    @DeleteMapping("/posts/delete/{id}")
    public ResponseEntity<Response> deletePost(@PathVariable("id") Long id) {
        System.out.println("===================================================");
        System.out.println("===================================================");
        System.out.println("===================================================");
        System.out.println("===================================================");
        System.out.println("===================================================");

        Post p = this.postService.getById(id);
        if (p == null) {
            return ResponseEntity.notFound().build();
        }
        this.postService.deletePost(p);
        return ResponseEntity.ok().body(new Response(true, "post deleted succesfuly"));
    }

    @GetMapping("/reports")
    public ResponseEntity<List<UserDataResponse>> getAllReports() {
        List<UserDataResponse> users = this.adminService.getAllUsers();
        if (users == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(users);
    }

    @PostMapping("/profile/info/state")
    public ResponseEntity<Response> updateProfileState(@RequestBody UserDataResponse userData,
            Authentication authentication) {
        if (userData == null) {
            return ResponseEntity.badRequest().body(new Response(false, "user not valid"));
        }
        // String username = authentication.getName();
        this.adminService.updateUserState(userData.getUsername(), userData.getState());
        return ResponseEntity.ok().body(new Response(true, "state updated succesfuly"));
    }

    @GetMapping("/search/users/{content}")
    public ResponseEntity<List<UserDataResponse>> search(@PathVariable("content") String content, Authentication authentication) {
        if (content == null) {
            return ResponseEntity.badRequest().body(null);
        }
        System.out.println("=====================================");
        System.out.println("=====================================");
        System.out.println("=====================================");
        System.out.println("content is : " + content);
        List<UserDataResponse> list = this.adminService.findAll(content);
        return ResponseEntity.ok().body(list);
    }

    @PostMapping("/post/{id}/update/state") 
    public ResponseEntity<PostDataResponse> updatePostState(@PathVariable("id") Long id, @RequestBody PostState newState) {
        Post post = this.postService.getById(id);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }
        System.out.println("post id : " + id);
        System.out.println("post state is : " + newState.toString());
        this.postService.updatePostState(post, newState);
        return ResponseEntity.ok().body(PostDataResponse.convert(post));
    }
}
