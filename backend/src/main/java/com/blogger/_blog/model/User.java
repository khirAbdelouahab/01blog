package com.blogger._blog.model;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.blogger._blog.enums.UserRole;
import com.blogger._blog.enums.UserState;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;


@Entity
@Table(name = "users")
public class User  {
      @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    
    @Column(nullable = false, unique = true, length = 20)
    private String username;
    
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 30, message = "Full name must be between 2 and 30 characters")
    @Column(nullable = false, length = 30)
    private String fullname;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    @Column(nullable = false, unique = true)
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Column(nullable = false)
    private String password;
    private String avatar;
    private UserRole role;
    @Size(max = 5000, message = "About section cannot exceed 5000 characters")
    @Column(nullable = true, length = 5000)
    private String about;
    @Column(nullable = true)
    private UserState state;

    @OneToMany(mappedBy = "reportedUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserReport> reportsAboutMe;
    
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserReport> reportsICreated;
    
    public User() {
        this.role = UserRole.user;      // Set default role
        this.state = UserState.active;
    }
    public User(String fullname,String username, String email, String password, UserState state) {
        this.fullname = fullname;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = UserRole.user;
        this.state = state;
        try {
            String path = this.getProfilePath();
            this.avatar = path + "/profileImage.jpeg";
        } catch (Exception e) {
            this.avatar = "/profileImage.jpeg";
        }
    }

    private String getProfilePath() throws IOException {
        Path uploadPath = Paths.get("profileImages");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        return uploadPath.toAbsolutePath().toString();
    }
    public User(String fullname,String username, String email, String password,String about,UserState state) {
        this.fullname = fullname;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = UserRole.user;
        this.about = about;
        this.state = state;
        try {
            String path = this.getProfilePath();
            this.avatar = path + "/profileImage.jpeg";
        } catch (Exception e) {
            this.avatar = "/profileImage.jpeg";
        }
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

    public UserState getState() {
        return this.state;
    }

    public void setState(UserState state) {
        this.state=state;
    }

    public String getAbout() {
        return this.about;
    }
    public void setAbout(String about) {
        this.about = about;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setDefaultAvatar() {
        try {
            this.avatar = this.getProfilePath() + "/profileImage.jpeg";
        } catch (Exception e) {
            this.avatar = "";
        }
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getRoles() {
        return this.role.toString();
    }

}
