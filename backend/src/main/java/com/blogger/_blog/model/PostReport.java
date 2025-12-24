package com.blogger._blog.model;

import java.util.Date;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.blogger._blog.enums.ReportReason;
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
@Table(name = "postReport")
@EntityListeners(AuditingEntityListener.class)
public class PostReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = true)
    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @JsonIgnore
    private Post post;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User author;
    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date created_at;
    @Column(nullable = false)
    private ReportReason reason;
    public PostReport() {}
    public PostReport(Post post, User author,ReportReason reason) {
        this.post=post;
        this.author=author;
        this.reason=reason;
    }
    @PrePersist
    protected void onCreate() {
        created_at = new Date();
    }
    public Long getId() {
        return this.id;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content=content;
    }
    public Post getPost() {
        return this.post;
    }
    public void setPost(Post post) {
        this.post=post;
    }
    public User getAuthor() {
        return this.author;
    }
    public void setAuthor(User author) {
        this.author=author;
    }
    public Date getCreated_at() {
        return this.created_at;
    }
    public ReportReason getReason() {
        return this.reason;
    }
    public void setReason(ReportReason reason) {
        this.reason = reason;
    }
}
