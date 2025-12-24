package com.blogger._blog.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blogger._blog.Repository.ReportPostRepository;
import com.blogger._blog.details.ReportPostData;
import com.blogger._blog.enums.ReportReason;
import com.blogger._blog.model.Post;
import com.blogger._blog.model.PostReport;
import com.blogger._blog.model.User;

@Service
public class ReportPostService {
    
    @Autowired
    private ReportPostRepository reportPostRepository;

    public PostReport create(Post post,User author,ReportReason reason) {
        if (post == null || author == null) {
            return null;
        }
        PostReport report = new PostReport(post, author, reason);
        return this.reportPostRepository.save(report);
    }

    public PostReport create(Post post,User author,ReportReason reason,String content) {
        if (post == null || author == null) {
            return null;
        }
        PostReport report = new PostReport(post, author, reason);
        report.setContent(content);
        return this.reportPostRepository.save(report);
    }

    public List<ReportPostData> getAllbyPost(Long postId) {
        List<PostReport> arrayReports = this.reportPostRepository.findAllByPostId(postId);
        return ReportPostData.convert(arrayReports);
    }

    public List<ReportPostData> getReportsData() {
       List<PostReport> arrayReports = this.reportPostRepository.findAll();
       System.out.println("=================================================================");
       System.out.println("=================================================================");
       System.out.println("=================================================================");
       System.out.println("=================================================================");
       System.out.println("reports: " + arrayReports.toString());

        return ReportPostData.convert(arrayReports);
    }


}
