package com.blogger._blog.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.blogger._blog.enums.PostState;
import jakarta.persistence.*;

@Entity
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 150)
    private String title;
    @Column(nullable = false, length = 3000)
    private String content;
    @Column(nullable = false)
    private String category;
    @Column(nullable = true)
    private Date creationDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User author;
    @Column(nullable = true)
    private PostState state;
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private List<MediaUpload> mediaUploads = new ArrayList<>();
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private List<Reaction> reactions = new ArrayList<>();
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private List<PostReport> reports = new ArrayList<>();

    // Add getter and setter
    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public Post() {
    }

    public Post(String title, String content, String category) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.state = PostState.VISIBLE;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public PostState getState() {
        return state;
    }

    public void setState(PostState state) {
        this.state = state;
    }

    public List<MediaUpload> getMediaUploads() {
        return mediaUploads;
    }

    public void setMediaUploads(List<MediaUpload> mediaUploads) {
        this.mediaUploads = mediaUploads;
    }

    public List<Reaction> getReactions() {
        return this.reactions;
    }

    public void setReactions(List<Reaction> reactions) {
        this.reactions = reactions;
    }

    public List<Comment> getComments() {
        return this.comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

}
