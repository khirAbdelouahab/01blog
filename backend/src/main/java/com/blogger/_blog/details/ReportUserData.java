package com.blogger._blog.details;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.blogger._blog.enums.ReportReason;
import com.blogger._blog.model.UserReport;
public class ReportUserData {
    private Long id;
    private String content;
    private UserDataResponse reportedUser;
    private UserDataResponse author;
    private Date created_at;
    private ReportReason reason;
    
    public ReportUserData() {
    }
    public ReportUserData(UserDataResponse author, Date created_at,
            ReportReason reason) {
        this.author = author;
        this.created_at = created_at;
        this.reason = reason;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id=id;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UserDataResponse getReportedUser() {
        return this.reportedUser;
    }

    public void setReportedUser(UserDataResponse reportedUser) {
        this.reportedUser = reportedUser;
    }

    public UserDataResponse getAuthor() {
        return this.author;
    }

    public void setAuthor(UserDataResponse author) {
        this.author = author;
    }

    public Date getCreated_at() {
        return this.created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public ReportReason getReason() {
        return this.reason;
    }

    public void setReason(ReportReason reason) {
        this.reason = reason;
    }

    public static List<ReportUserData> convert(List<UserReport> reports) {
        List<ReportUserData> result = new ArrayList<>();
        for (int i = 0; i < reports.size(); i++) {
            ReportUserData rePostData = ReportUserData.convert(reports.get(i));
            result.add(rePostData);
        }
        return result;
    }

    public static ReportUserData convert(UserReport report) {
        ReportUserData data = new ReportUserData();
        data.setId(report.getId());
        data.setAuthor(UserDataResponse.convert(report.getAuthor()));
        data.setReportedUser(UserDataResponse.convert(report.getReportedUser()));
        data.setReason(report.getReason());
        data.setCreated_at(report.getCreated_at());
        data.setContent(report.getContent());
        return data;
    }
}
