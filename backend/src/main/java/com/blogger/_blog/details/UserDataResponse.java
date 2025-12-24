package com.blogger._blog.details;

import java.util.ArrayList;
import java.util.List;

import com.blogger._blog.enums.UserRole;
import com.blogger._blog.enums.UserState;
import com.blogger._blog.model.User;


public class UserDataResponse {
    private Long id;
    private String username;
    private String fullname;
    private String email;
    private String avatar;
    private String role;
    private String about;
    private UserState state;

    public UserDataResponse() {}

    public UserDataResponse(Long id,String username,String fullname,String email,String avatar,UserRole role,String about, UserState state) {
        this.id=id;
        this.fullname=fullname;
        this.username=username;
        this.email=email;
        this.avatar=avatar;
        this.role = role.toString();
        this.about=about;
        this.state = state;

    }

    public UserDataResponse(Long id,String username,String fullname,String avatar,String about, UserState state) {
        this.id=id;
        this.fullname=fullname;
        this.username=username;
        this.avatar=avatar;
        this.email = "";
        this.role = UserRole.user.toString();
        this.about=about;
        this.state = state;
    }

    public Long getId() {
        return this.id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String name) {
        this.username = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public String getFullname() {
        return fullname;
    }
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
    public String getAbout() {
        return this.about;
    }
    public void setAbout(String about) {
        this.about = about;
    }
    
    public UserState getState() {
        return this.state;
    }

    public void setState(UserState state) {
        this.state=state;
    }
    public static UserDataResponse convert(User u) {
        if (u == null) {
            return null;
        }
        UserDataResponse user = new UserDataResponse(u.getId(), u.getUsername(), u.getFullname(),
        u.getEmail(), u.getAvatar(), u.getRole(),u.getAbout(),u.getState());
        user.setState(u.getState());
        return user;
    }
    public static List<UserDataResponse> convert(List<User> users) {
        if (users == null) {
            return null;
        }
        List<UserDataResponse> listOfUsers = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            listOfUsers.add(UserDataResponse.convert(users.get(i)));
        }
        return listOfUsers;
    }

}
