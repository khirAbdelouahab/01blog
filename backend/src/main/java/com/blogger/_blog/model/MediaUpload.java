package com.blogger._blog.model;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.blogger._blog.enums.MediaType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "mediaUpload")
@EntityListeners(AuditingEntityListener.class)
public class MediaUpload {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Post post;
    @Column(nullable = false)
    private String media_path;
    @Column(nullable = false)
    private MediaType media_type;
    @Column(nullable = false)
    private Long file_size;
    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date created_at;
    @Column(nullable = false, length = 2000)
    private String content;
    public MediaUpload() {}

    public MediaUpload(Post post,String media_path,MediaType media_type,Long file_size,String content) {
        this.post = post;
        try {    
            this.media_path = this.getMediaPath() + media_path;
        } catch (Exception e) {
            this.media_path = media_path;
        }
        this.media_type = media_type;
        this.file_size = file_size;
        this.content=content;
    }

    @PrePersist
    protected void onCreate() {
        created_at = new Date();
    }
    public Long getId() {
        return this.id;
    }

    private String getMediaPath() throws IOException {
        Path uploadPath = Paths.get("uploads");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        return uploadPath.toAbsolutePath().toString();
    }

    public Post getPost() {
        return this.post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getMedia_path() {
        return this.media_path;
    }

    public void setMedia_path(String value) {
        this.media_path = value;
    }

    public MediaType getMedia_type() {
        return this.media_type;
    }

    public void setMedia_type(MediaType value) {
        this.media_type = value;
    }

    public Long getFile_size() {
        return this.file_size;
    }

    public void setFile_size(Long value) {
        this.file_size = value;
    }

    public Date getCreated_at() {
        return this.created_at;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content=content;
    }

}
