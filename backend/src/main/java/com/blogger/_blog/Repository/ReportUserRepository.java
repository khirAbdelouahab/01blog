package com.blogger._blog.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.blogger._blog.model.UserReport;

public interface ReportUserRepository extends JpaRepository<UserReport,Long> {
    UserReport save(UserReport userReport);
    void delete(UserReport userReport);
    List<UserReport> findAll();
}
