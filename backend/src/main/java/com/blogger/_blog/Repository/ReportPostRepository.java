package com.blogger._blog.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.blogger._blog.model.PostReport;

public interface ReportPostRepository extends JpaRepository<PostReport,Long> {

    @Query("SELECT DISTINCT r FROM PostReport r WHERE r.post.id = ?1")
    public List<PostReport> findAllByPostId(Long postId);
}
