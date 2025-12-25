package com.blogger._blog.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blogger._blog.details.ReportDataRequest;
import com.blogger._blog.details.ReportPostData;
import com.blogger._blog.details.Response;
import com.blogger._blog.enums.UserRole;
import com.blogger._blog.model.Post;
import com.blogger._blog.model.User;
import com.blogger._blog.service.PostService;
import com.blogger._blog.service.ReportPostService;
import com.blogger._blog.service.UserAuthenticationService;

@RestController
@RequestMapping("/api/report")
@CrossOrigin(origins = "http://localhost:4200")
public class ReportPostController {
    @Autowired
    private ReportPostService reportPostService;

    @Autowired
    private UserAuthenticationService uAuthenticationService;

    @Autowired
    private PostService postService;

    @GetMapping("/find/all")
    public ResponseEntity<List<ReportPostData>> getAllReports() {
        List<ReportPostData> reportsPostData = this.reportPostService.getReportsData();
        return ResponseEntity.ok().body(reportsPostData);
    }

    @GetMapping("/find/{postId}")
    public ResponseEntity<List<ReportPostData>> getAllReports(@PathVariable("postId") Long postId) {
        List<ReportPostData> reportsPostData = this.reportPostService.getAllbyPost(postId);
        return ResponseEntity.ok().body(reportsPostData);
    }

    @PostMapping("/post/new")
    public ResponseEntity<Response> create(@RequestBody ReportDataRequest reportData, Authentication authentication) {
        User user = this.uAuthenticationService.findByUsername(authentication.getName());
        Post post = this.postService.getById(reportData.getReportedId());
        if (post == null || user == null) {
            return ResponseEntity.badRequest().body(null);
        }
        if (post.getAuthor().getUsername().equals(authentication.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response(false, "you can't report your post"));
        }

        if (post.getAuthor().getRole().equals(UserRole.admin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response(false, "you can't report admin post"));
        }
        try {
            this.reportPostService.create(post, user, reportData.getReason(),
                    reportData.getContent());
            return ResponseEntity.ok().body(new Response(true, "report submitted succesfuly"));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.internalServerError().body(new Response(false, e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new Response(false, e.getMessage()));
        }
    }
}
