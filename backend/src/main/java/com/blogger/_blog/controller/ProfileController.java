package com.blogger._blog.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.blogger._blog.details.PostDataResponse;
import com.blogger._blog.details.ProfileFollowStats;
import com.blogger._blog.details.ReportDataRequest;
import com.blogger._blog.details.Response;
import com.blogger._blog.details.UserDataResponse;
import com.blogger._blog.details.UserProfileDataResponse;
import com.blogger._blog.model.Post;
import com.blogger._blog.model.User;
import com.blogger._blog.service.FollowingService;
import com.blogger._blog.service.PostService;
import com.blogger._blog.service.ProfileService;
import com.blogger._blog.service.ReportPostService;
import com.blogger._blog.service.UserAuthenticationService;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "http://localhost:4200")
public class ProfileController {
    @Autowired
    private UserAuthenticationService userAuthenticationService;
    @Autowired
    private PostService postService;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private FollowingService followingService;
    @Autowired
    private ReportPostService reportPostService;

    @GetMapping("/image/{username}")
    public ResponseEntity<Resource> getProfileImage(@PathVariable String username) {
        User user = this.userAuthenticationService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            Path filePath = Paths.get(user.getAvatar());
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            }
            System.out.println("❌ File does NOT exist at: " + filePath.toAbsolutePath());
            System.out.println("Parent directory exists: " + Files.exists(filePath.getParent()));
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/image/me")
    public ResponseEntity<Resource> getMyProfileImage(Authentication authentication) {
        String username = null;
        if (authentication != null) {
            username = authentication.getName();
        } else {
            try {
                Path filePath1 = Paths.get("profileImages/profileImage_2.png");
                Resource resource1 = new UrlResource(filePath1.toUri());
                if (resource1.exists() && resource1.isReadable()) {
                    String contentType1 = Files.probeContentType(filePath1);
                    if (contentType1 == null) {
                        contentType1 = "application/octet-stream";
                    }
                    return ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType(contentType1))
                            .body(resource1);
                }
            } catch (Exception e) {
                return ResponseEntity.internalServerError().build();
            }
            return ResponseEntity.internalServerError().build();
        }
        User user = this.userAuthenticationService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            // Path filePath =
            // Paths.get("/home/abouchik/Desktop/01-blogger/backend/profileImages/profileImage.jpeg");
            Path filePath = Paths.get(user.getAvatar());
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            }
            System.out.println("❌ File does NOT exist at: " + filePath.toAbsolutePath());
            System.out.println("Parent directory exists: " + Files.exists(filePath.getParent()));
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/info/{username}")
    public ResponseEntity<UserProfileDataResponse> getUserProfile(@PathVariable String username,
            Authentication authentication) {
        List<PostDataResponse> postsDataResponse = this.postService.getThemByUser(username);
        UserProfileDataResponse userProfileDataResponse = new UserProfileDataResponse(postsDataResponse);
        UserDataResponse userDataResponse = UserDataResponse
                .convert(this.userAuthenticationService.findByUsername(username));
        userProfileDataResponse.setUserDataResponse(userDataResponse);
        ProfileFollowStats profileFollowStats = this.followingService.getUserProfileStats(username);
        if (Objects.equals(username, authentication.getName())) {
            userProfileDataResponse.setIsConnectedUser(true);
        }
        if (userProfileDataResponse == null || userProfileDataResponse.getPostDataResponse() == null
                || userProfileDataResponse.getUserDataResponse() == null) {
            return ResponseEntity.notFound().build();
        }
        userProfileDataResponse.setProfileFollowStats(profileFollowStats);
        String myName = authentication.getName();
        boolean isSubscribedByMe = this.followingService.isSubscribedByMe(myName, username);
        userProfileDataResponse.setIsSubscribedByMe(isSubscribedByMe);
        return ResponseEntity.ok(userProfileDataResponse);
    }

    @GetMapping("/info/me")
    public ResponseEntity<UserProfileDataResponse> getConnectedUserProfile(Authentication authentication) {
        String username = authentication.getName();
        List<PostDataResponse> postsDataResponse = this.postService.getThemByUser(username);
        UserProfileDataResponse userProfileDataResponse = new UserProfileDataResponse(postsDataResponse);
        userProfileDataResponse.setIsConnectedUser(true);
        UserDataResponse userDataResponse = UserDataResponse
                .convert(this.userAuthenticationService.findByUsername(username));
        userProfileDataResponse.setUserDataResponse(userDataResponse);
        if (userProfileDataResponse == null || userProfileDataResponse.getPostDataResponse() == null
                || userProfileDataResponse.getUserDataResponse() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userProfileDataResponse);
    }

    @PostMapping("/info/about")
    public ResponseEntity<UserDataResponse> setAboutUser(@RequestBody UserDataResponse userDataResponse) {
        System.out.println("userDataResponse: " + userDataResponse.getAbout());
        UserDataResponse u = this.userAuthenticationService.updateUserAbout(userDataResponse);
        if (u == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(u);
    }

    @PostMapping("/info/image")
    public ResponseEntity<Response> updateProfileImage(@RequestParam("file") MultipartFile file,
            Authentication authentication) {
        if (file == null) {
            return ResponseEntity.badRequest().body(new Response(true, "image updated succesfuly"));
        }
        String username = authentication.getName();
        this.profileService.updateProfileImage(username, file);
        return ResponseEntity.ok().body(new Response(true, "image updated succesfuly"));
    }

    @PostMapping("/report")
    public ResponseEntity<Response> report(@RequestBody ReportDataRequest reportData, Authentication authentication) {
        User user = this.userAuthenticationService.findByUsername(authentication.getName());
        User reportedUser = this.userAuthenticationService.findById(reportData.getReportedId());
        if (reportedUser == null || user == null) {
            return ResponseEntity.notFound().build();
        }
        if (reportedUser.getUsername().equals(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response(false, "you can't report yourself"));
        }
        try {
            this.reportPostService.create(reportedUser, user, reportData.getReason(),
                    reportData.getContent());
            return ResponseEntity.ok().body(new Response(true, "report submitted succesfuly"));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.internalServerError().body(new Response(false, e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new Response(false, e.getMessage()));
        }
    }

}
