package com.blogger._blog.details;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.blogger._blog.model.Comment;
public class CommentDataResponse {
    private Long id;
    private String content;
    private Date creationDate;
    private UserDataResponse author;
    private boolean isCreatedByConnectedUser;


    public CommentDataResponse(){}
    public CommentDataResponse(Long id, String content, Date creationDate, UserDataResponse author){
        this.id=id;
        this.content = content;
        this.creationDate = creationDate;
        this.author = author;
        this.isCreatedByConnectedUser = false;
    }


    public boolean getIsCreatedByConnectedUser() {
        return this.isCreatedByConnectedUser;
    }

    public void setIsCreatedByConnectedUser(boolean value) {
        this.isCreatedByConnectedUser = value;
    }

    public CommentDataResponse setAuthor(UserDataResponse author) {
        this.author = author;
        return this;
    }
    public UserDataResponse getAuthor() {
        return author;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public static CommentDataResponse convert(Comment comment) {
        if (comment == null) {
            return null;
        }
        return new CommentDataResponse(comment.getId(), comment.getContent(), comment.getCreated_at(), UserDataResponse.convert(comment.getAuthor()));
    }

    public static List<CommentDataResponse> convert(List<Comment> comments) {
        if (comments == null) {
            return null;
        }
        List<CommentDataResponse> result = new ArrayList<>();
        for (int index = 0; index < comments.size(); index++) {
            Comment comment = comments.get(index);
            CommentDataResponse commentDataResponse = new CommentDataResponse(comment.getId(), comment.getContent(), comment.getCreated_at(), UserDataResponse.convert(comment.getAuthor()));
            result.add(commentDataResponse);
        }
        return result;
    }

    public static List<CommentDataResponse> convert(List<Comment> comments, String AuthorName) {
        if (comments == null) {
            return null;
        }
        List<CommentDataResponse> result = new ArrayList<>();
        for (int index = 0; index < comments.size(); index++) {
            Comment comment = comments.get(index);
            CommentDataResponse commentDataResponse = new CommentDataResponse(comment.getId(), comment.getContent(), comment.getCreated_at(), UserDataResponse.convert(comment.getAuthor()));
            if (comment.getAuthor().getUsername().equals(AuthorName)) {
                commentDataResponse.setIsCreatedByConnectedUser(true);
            }
            result.add(commentDataResponse);
        }
        return result;
    }

}
