package com.blogger._blog.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.blogger._blog.Repository.ReportPostRepository;
import com.blogger._blog.Repository.ReportUserRepository;

import com.blogger._blog.details.ReportPostData;
import com.blogger._blog.details.ReportUserData;
import com.blogger._blog.enums.ReportReason;
import com.blogger._blog.model.Post;
import com.blogger._blog.model.PostReport;
import com.blogger._blog.model.User;
import com.blogger._blog.model.UserReport;

@Service
public class ReportPostService {
    
    @Autowired
    private ReportPostRepository reportPostRepository;
    @Autowired
    private ReportUserRepository reportUserRepository;

    public PostReport create(Post post,User author,ReportReason reason) {
        if (post == null || author == null) {
            return null;
        }
        PostReport report = new PostReport(post, author, reason);
        return this.reportPostRepository.save(report);
    }

    public PostReport create(Post post, User author, ReportReason reason, String content) {
        if (post == null || author == null) {
            throw new IllegalArgumentException("Post and author cannot be null");
        } 
    
        if (reason == null) {
            throw new IllegalArgumentException("Report reason cannot be null");
        }
    
        if (content != null && content.length() > 200) {
            throw new IllegalArgumentException("Report content cannot exceed 200 characters");
        }
    
        try {
            PostReport report = new PostReport(post, author, reason);
            report.setContent(content);
            return this.reportPostRepository.save(report);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Failed to create report: " + e.getMostSpecificCause().getMessage());
        }
    }

    public UserReport create(User reportedUser, User author, ReportReason reason, String content) {
        if (reportedUser == null || author == null) {
            throw new IllegalArgumentException("reportedUser and author cannot be null");
        } 
    
        if (reason == null) {
            throw new IllegalArgumentException("Report reason cannot be null");
        }
    
        if (content != null && content.length() > 200) {
            throw new IllegalArgumentException("Report content cannot exceed 200 characters");
        }
    
        try {
            UserReport report = new UserReport(reportedUser, author, reason);
            report.setContent(content);
            return this.reportUserRepository.save(report);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Failed to create report: " + e.getMostSpecificCause().getMessage());
        }
    }
    public List<ReportPostData> getAllbyPost(Long postId) {
        List<PostReport> arrayReports = this.reportPostRepository.findAllByPostId(postId);
        return ReportPostData.convert(arrayReports);
    }

    public List<ReportPostData> getReportsData() {
       List<PostReport> arrayReports = this.reportPostRepository.findAll();
        return ReportPostData.convert(arrayReports);
    }

    public List<ReportUserData> getReportsDataForUsers() {
       List<UserReport> arrayReports = this.reportUserRepository.findAll();
        return ReportUserData.convert(arrayReports);
    }


}
