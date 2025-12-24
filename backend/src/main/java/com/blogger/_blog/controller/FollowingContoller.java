package com.blogger._blog.controller;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.blogger._blog.details.OtherUserData;
import com.blogger._blog.details.SubscribeData;
import com.blogger._blog.service.FollowingService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class FollowingContoller {
    @Autowired
    private FollowingService followingService;

    @GetMapping("/others")
    public ResponseEntity<List<OtherUserData>> getOthers(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(this.followingService.findOthers(username));
    }

    @PostMapping("/follow/{username}")
    public ResponseEntity<SubscribeData> follow(@PathVariable("username") String username, Authentication authentication) {
        String senderName = authentication.getName();
        SubscribeData result = this.followingService.Subsrcibe(senderName, username);
        return ResponseEntity.ok(result);
    }
}
