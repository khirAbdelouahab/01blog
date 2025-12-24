package com.blogger._blog.service;

import com.blogger._blog.Repository.UserRepository;
import com.blogger._blog.details.UserData;
import com.blogger._blog.details.UserDataResponse;
import com.blogger._blog.enums.UserRole;
import com.blogger._blog.enums.UserState;
import com.blogger._blog.model.User;

import jakarta.persistence.Tuple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class UserAuthenticationService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JWTService jwtService;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
    public User createUser(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        user.setRole(UserRole.user);
        user.setState(UserState.active);
        user.setDefaultAvatar();
        return userRepository.save(user);
    }

    public User save(User user) {
        return this.userRepository.save(user);
    }

    public String extractUsername(String token) {
        return jwtService.extractUsername(token);
    }

    public boolean existsByUsername(String username) {
        return this.userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }
    public String generateToken(User user) {
        return jwtService.generateToken(user);
    }

    public String generateToken(UserDataResponse user) {
        return jwtService.generateToken(user);
    }

    public User findByUsername(String username)  {
        return userRepository.findByUsername(username).orElse(null);
    }

    public UserData extractUserDetails(Tuple data) {
        if (data == null) {
            return null;
        }
        User user = data.get("user", User.class);
        Long count = data.get("notificationsCount",Long.class);
        if (user == null || count == null) {
            return null;
        }
        UserData details = new UserData(UserDataResponse.convert(user), count);
        return details;
    }

    public User extractUserFromTuple(Tuple data) {
        if(data == null) {
            return null;
        }
        User user = data.get("user", User.class);
        if (user == null ) {
            return null;
        }
        return user;
    }
    public Tuple findUserDetails(String username) {
        return this.userRepository.getUserDetails(username);
    }

    public boolean passwordEquals(String password,String hashedPassword) {
        return encoder.matches(password,hashedPassword);
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findAllUsers() {
        return this.userRepository.findAllUsers();
    }

    public UserDataResponse updateUserAbout(UserDataResponse user) {
        User u = this.userRepository.findByUsername(user.getUsername()).orElse(null);
        if (u == null) {
            return null;
        }
        u.setAbout(user.getAbout());
        return UserDataResponse.convert(this.userRepository.save(u));
    }

    public List<UserDataResponse> findAll(String content) {
        List<User> users;
        if (content.equals("")) {
            users = this.userRepository.findAllUsers();
        } else {
            users = this.userRepository.findAllUsers(content);
        }
        List<UserDataResponse> filteredByContent = UserDataResponse.convert(users);
        return filteredByContent;
    }

    public void delete(User user) {
        this.userRepository.delete(user);
    }
}
