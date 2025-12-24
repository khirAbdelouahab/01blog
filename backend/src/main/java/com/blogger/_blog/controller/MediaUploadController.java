package com.blogger._blog.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.blogger._blog.Repository.MediaUploadRepository;
import com.blogger._blog.model.MediaUpload;

@RestController
@RequestMapping("/api/media")
@CrossOrigin(origins = "http://localhost:4200", 
             allowedHeaders = "*",
             exposedHeaders = "*")
public class MediaUploadController {

    @Autowired
    private MediaUploadRepository mediaUploadRepository;
    @GetMapping("/post/{id}")
    public ResponseEntity<List<Resource>> getMediaByPost(@PathVariable Long id) {

        List<MediaUpload> mediaList = this.mediaUploadRepository.findByPostID(id);
        List<Resource> resources = new ArrayList<>();
        for (MediaUpload media : mediaList) {
            try {
                Path filePath = Paths.get(media.getMedia_path());
                Resource resource = new UrlResource(filePath.toUri());
                if (resource.exists() && resource.isReadable()) {
                    resources.add(resource);
                }
            } catch (Exception e) {

            }
        }

        return ResponseEntity.ok(resources);
    }

    
    @GetMapping("/file/{id}")
    public ResponseEntity<Resource> getMediaById(@PathVariable("id") Long id) {
        MediaUpload mediaItem  = this.mediaUploadRepository.findById(id).orElse(null);
        if (mediaItem  == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            String storedPath = mediaItem.getMedia_path();
            // FIX: Remove duplicate drive letter if present (C:\c\... -> c:\...)
            if (storedPath.matches("^[A-Za-z]:\\\\[A-Za-z]:.*")) {
                storedPath = storedPath.substring(3); // Remove first "C:\"
                System.out.println("Fixed duplicate drive letter");
            }
            Path filePath = Paths.get(storedPath);
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
}
