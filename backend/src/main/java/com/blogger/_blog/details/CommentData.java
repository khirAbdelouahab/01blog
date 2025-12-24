package com.blogger._blog.details;

public class CommentData {
    private String content;
    private Long postId;
    public CommentData() {}

    public CommentData(String content,Long postId) {
        this.content=content;
        this.postId=postId;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String value) {
        this.content=value;
    }

    public Long getPostId() {
        return this.postId;
    }

    public void setPostId(Long value) {
        this.postId=value;
    }

}
