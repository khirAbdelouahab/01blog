package com.blogger._blog.controller;

import com.blogger._blog.details.JwtResponse;
import com.blogger._blog.details.Response;
import com.blogger._blog.details.UserData;
import com.blogger._blog.enums.UserState;
import com.blogger._blog.model.User;
import com.blogger._blog.service.*;
import jakarta.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200") // Allow Angular app to access
public class UserAuthenticationContoller {
    @Autowired
    private UserAuthenticationService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        // Check if username and password are "admin"
        if (loginRequest == null || loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Response(false, "Login failed"));
        }
        Tuple data = userService.findUserDetails(loginRequest.getUsername());
        User user = this.userService.extractUserFromTuple(data);
        UserData userData = this.userService.extractUserDetails(data);
        if (user != null && userService.passwordEquals(loginRequest.getPassword(), user.getPassword())) {
            if (user.getState().equals(UserState.banned)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response(false, "you are banned"));
            }
            String token = userService.generateToken(user);
            return ResponseEntity.ok(new JwtResponse(token, userData.getUser(), userData.getNotificationsCount()));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Response(false, "Invalid creadentials"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Response> register(@Validated @RequestBody User registerRequest) {
        if (this.userService.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response(false,
                    String.format("Username {%s} is already taken", registerRequest.getUsername())));
        }

        if (this.userService.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response(false,
                    String.format("Email {%s} is already taken", registerRequest.getEmail())));
        }
        try {
            User created = userService.createUser(registerRequest);
            return ResponseEntity.ok(new Response(true, "Register successful: " + created.getId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new Response(false, e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }
}