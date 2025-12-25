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
@Table(name = "userReport")
@EntityListeners(AuditingEntityListener.class)
public class UserReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = true, length = 200)
    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE) 
    private User reportedUser;
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
    public UserReport() {}
    public UserReport(User reportedUser, User author,ReportReason reason) {
        this.reportedUser=reportedUser;
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
    public User getReportedUser() {
        return this.reportedUser;
    }
    public void setReportedUser(User user) {
        this.reportedUser=user;
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
