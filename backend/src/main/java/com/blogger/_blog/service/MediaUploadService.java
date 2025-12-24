package com.blogger._blog.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.blogger._blog.CustomExceptions.FileSizeLimitExceededException;
import com.blogger._blog.CustomExceptions.TotalSizeLimitExceededException;
import com.blogger._blog.Repository.MediaUploadRepository;
import com.blogger._blog.details.MediaUploadDataResponse;
import com.blogger._blog.enums.MediaType;
import com.blogger._blog.model.MediaUpload;
import com.blogger._blog.model.Post;

@Service
public class MediaUploadService {
    @Autowired
    private MediaUploadRepository mediaUploadRepository;

    public void Create(Post post, MultipartFile[] files, String[] mediaContents) throws IOException {

        long maxFileSize = 100 * 1024 * 1024L; // 100MB
        long maxRequestSize = 500 * 1024 * 1024L; // 500MB
        long totalSize = 0;
        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            if (file == null) {
                throw new IOException("File is null");
            }
            if (file.isEmpty()) {
                continue;
            }
            if (file.getSize() + totalSize > maxRequestSize) {
                throw new TotalSizeLimitExceededException(totalSize + file.getSize(), maxRequestSize);
            }
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/") && !contentType.startsWith("video/")) {
                throw new IOException("Only images and videos allowed");
            }

            if (file.getSize() > maxFileSize) {
                throw new FileSizeLimitExceededException(file.getOriginalFilename(), file.getSize(), maxFileSize);
            }
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                throw new IOException("File is null");
            }
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + extension;
            Path uploadPath = Paths.get("uploads");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(newFilename);
            file.transferTo(filePath);
            MediaType fileType = contentType.substring(0, 5).equals("image") ? MediaType.Image : MediaType.Video;
            String fileContent = (mediaContents != null && i < mediaContents.length)
                    ? mediaContents[i]
                    : "";
            if (fileContent.length() > 2000) {
                throw new DataIntegrityViolationException("Conent is too long");
            }

            MediaUpload mediaUpload = new MediaUpload(
                    post,
                    "/" + newFilename,
                    fileType,
                    file.getSize(),
                    fileContent);
            this.mediaUploadRepository.save(mediaUpload);
            totalSize += file.getSize();
        }
    }

    public void Update(Post post, MultipartFile[] files, MediaUploadDataResponse[] mediaData) throws IOException {

         long maxFileSize = 100 * 1024 * 1024L; // 100MB
        long maxRequestSize = 500 * 1024 * 1024L; // 500MB
        long totalSize = 0;
        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            if (file.isEmpty()) {
                continue;
            }

            if (file.getSize() + totalSize > maxRequestSize) {
                throw new TotalSizeLimitExceededException(totalSize + file.getSize(), maxRequestSize);
            }
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/") && !contentType.startsWith("video/")) {
                throw new IOException("Only images and videos allowed");
            }
            if (file.getSize() > maxFileSize) {
                throw new FileSizeLimitExceededException(file.getOriginalFilename(), file.getSize(), maxFileSize);
            }
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                throw new IOException("File is null");
            }
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + extension;
            Path uploadPath = Paths.get("uploads");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(newFilename);
            file.transferTo(filePath);
            MediaType fileType = contentType.substring(0, 5).equals("image") ? MediaType.Image : MediaType.Video;
            String fileContent = (mediaData != null && i < mediaData.length)
                    ? mediaData[i].getContent()
                    : "";
            MediaUpload mediaUpload = new MediaUpload(
                    post,
                    "/" + newFilename,
                    fileType,
                    file.getSize(),
                    fileContent);
            this.mediaUploadRepository.save(mediaUpload);
            totalSize += file.getSize();

        }
    }

    public List<MediaUpload> getMediaByPostId(Long postID) {
        List<MediaUpload> medias = this.mediaUploadRepository.findByPostID(postID);
        return medias;
    }

    public MediaUpload update(MediaUpload newMediaUpload) {
        if (newMediaUpload == null) {
            return null;
        }
        MediaUpload media = this.mediaUploadRepository.findById(newMediaUpload.getId()).orElse(null);
        if (media == null) {
            return null;
        }
        media.setContent(newMediaUpload.getContent());
        return this.mediaUploadRepository.save(media);
    }

    public void delete(List<MediaUploadDataResponse> medias) {
        for (int i = 0; i < medias.size(); i++) {
            this.delete(medias.get(i).getId());
        }
    }

    public void delete(Long mediaId) {
        this.mediaUploadRepository.deleteById(mediaId);
    }

    public void generatePostMedia(Long postID, List<MediaUploadDataResponse> medias) {
        List<MediaUpload> postMediaUploads = this.getMediaByPostId(postID);
        for (int index = 0; index < postMediaUploads.size(); index++) {
            MediaUpload m = postMediaUploads.get(index);
            MediaUploadDataResponse exists = medias.stream()
                    .filter(media -> media.getId().equals(m.getId())).findFirst().orElse(null);
            if (exists != null) {
                m.setContent(exists.getContent());
                this.mediaUploadRepository.save(m);
            } else {
                this.mediaUploadRepository.delete(m);
            }
        }
    }

    public void deletePostMedia(Post post) {
        List<MediaUpload> media = this.mediaUploadRepository.findByPostID(post.getId());
        for (MediaUpload mediaUpload : media) {
            this.mediaUploadRepository.delete(mediaUpload);
        }
    }
}
