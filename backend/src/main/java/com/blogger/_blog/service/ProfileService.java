package com.blogger._blog.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.blogger._blog.details.PostDataResponse;
import com.blogger._blog.details.UserDataResponse;
import com.blogger._blog.details.UserProfileDataResponse;
import com.blogger._blog.enums.UserState;
import com.blogger._blog.model.User;

@Service
public class ProfileService {
    @Autowired
    private PostService postService;
    @Autowired
    private UserAuthenticationService uAuthenticationService;

    @Transactional(readOnly = true) // Add this annotation
    public UserProfileDataResponse getUserProfile(String userName) {
        UserDataResponse userDataResponse = UserDataResponse
                .convert(this.uAuthenticationService.findByUsername(userName));
        List<PostDataResponse> postsDataResponse = this.postService.getThemByUser(userName);
        UserProfileDataResponse uProfileDataResponse = new UserProfileDataResponse(userDataResponse, postsDataResponse);
        return uProfileDataResponse;
    }

    public void updateProfileImage(String username, MultipartFile file) {
        User user = this.uAuthenticationService.findByUsername(username);
        try {
            String path = this.extractFilePath(file);
            user.setAvatar(path);
            this.uAuthenticationService.save(user);
        } catch (Exception e) {
            
        }
    }

    public void updateUserState(String username,UserState state) {
        User user = this.uAuthenticationService.findByUsername(username);
        if (user != null) {
            user.setState(state);
            this.uAuthenticationService.save(user);
        }
    }

    private String extractFilePath(MultipartFile file1) throws IOException {
        MultipartFile file = file1;
        Path uploadPath = Paths.get("profileImages");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        System.out.println("path : " + uploadPath.toAbsolutePath().toString());
        if (file.isEmpty()) {
            return uploadPath.toAbsolutePath().toString();
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IOException("Only images allowed");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IOException("File too large");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IOException("File is null");
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = UUID.randomUUID().toString() + extension;
        Path filePath = uploadPath.resolve(newFilename);
        file.transferTo(filePath);
        return uploadPath.toAbsolutePath().toString() + "/" + newFilename;
    }
}
