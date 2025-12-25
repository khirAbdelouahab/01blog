package com.blogger._blog.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.blogger._blog.details.PostDataResponse;
import com.blogger._blog.details.UserDataResponse;
import com.blogger._blog.enums.UserState;
import com.blogger._blog.model.Post;
import com.blogger._blog.model.User;

@Service
public class AdminService {
    @Autowired
    private UserAuthenticationService uService;

    @Autowired
    private PostService postService;

    public List<UserDataResponse> getAllUsers() {
        List<User> allUsers = this.uService.findAllUsers();
        if (allUsers == null) {
            return null;
        }
        return UserDataResponse.convert(allUsers);
    }

    public List<PostDataResponse> getAllPosts() {
        List<Post> allPosts = this.postService.getAllUsersPosts();
        if (allPosts == null) {
            return null;
        }
        return this.postService.convert(allPosts);
    }

    public void updateUserState(String username, UserState state) {
        User user = this.uService.findByUsername(username);
        if (user != null) {
            user.setState(state);
            try {
                this.uService.save(user);
            } catch (IllegalArgumentException e) {
               System.out.println("==========================");
               System.out.println("=======    ERROR   =======");
               System.out.println("==========================");

               System.out.println(e.getMessage());


            }           
        }
    }

    public List<UserDataResponse> findAll(String content) {
        List<UserDataResponse> filteredByContent = this.uService.findAll(content);
        System.out.println("mrigla");
        return filteredByContent;
    }
}
