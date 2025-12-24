package com.blogger._blog.details;

public class Response {
    private boolean success;
    private String message;
    private Long likes;


    // Constructors
    public Response() {}

    public Response(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public Response(boolean success, String message, Long likes) {
        this.success = success;
        this.message = message;
        this.likes = likes;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Long getLikes() {
        return this.likes;
    }

    public void setMessage(Long likes) {
        this.likes = likes;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
