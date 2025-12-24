package com.blogger._blog.details;

import com.blogger._blog.enums.ReportReason;

public class ReportDataRequest {
  private String content;
  private Long reportedId;
  private ReportReason reason;
  
  public ReportDataRequest() {}
  public String getContent() {
    return this.content;
  }
  public Long getReportedId() {
    return this.reportedId;
  }
  public ReportReason getReason() {
    return this.reason;
  }

}
