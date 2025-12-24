package com.blogger._blog.model;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reciever_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User reciever;
    @Column(nullable = false, length = 300)
    private String content;
    @Column(nullable = true)
    private boolean isRead;
    public Notification() {
    }
    public Notification(Post post, User reciever, String content) {
        this.post = post;
        this.reciever = reciever;
        this.content = content;
        this.isRead = false;
    }
    public Long getId() {
        return this.id;
    }
    public Post getPost() {
        return this.post;
    }
    public void setPost(Post post) {
        this.post = post;
    }
    public String getContent() {
       return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public User getReciever() {
        return this.reciever;
    }
    public void setReciever(User reciever) {
        this.reciever = reciever;
    }
    public boolean getRead() {
        return this.isRead;
    }
    public void setRead(boolean read) {
        this.isRead = read;
    }
}
