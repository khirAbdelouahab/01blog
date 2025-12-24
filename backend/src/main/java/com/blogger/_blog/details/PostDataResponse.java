package com.blogger._blog.details;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.blogger._blog.enums.PostState;
import com.blogger._blog.model.Post;


public class PostDataResponse {
    private Long id;
    private String title;
    private String content;
    private String category;
    private Date creationDate;
    private UserDataResponse author;
    private PostState state;
    private List<MediaUploadDataResponse> mediaUploads;
    private int likes;
    private List<CommentDataResponse> comments;
    private boolean createdByMe;

    
    public PostDataResponse(Long id,String title,String content,String category,Date creationDate) {
        this.id=id;
        this.title = title;
        this.content = content;
        this.category = category;
        this.creationDate = creationDate;
        this.author = null;
        this.mediaUploads = new ArrayList<>();
    }
   
    public PostDataResponse setAuthor(UserDataResponse author) {
        this.author = author;
        return this;
    }

    public boolean getCreatedByMe() {
        return this.createdByMe;
    }

    public void setCreatedByMe(boolean value) {
        this.createdByMe = value;
    }
    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public List<CommentDataResponse> getComments() {
        return comments;
    }

    public void setComments(List<CommentDataResponse> comments) {
        this.comments = comments;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public UserDataResponse getAuthor() {
        return author;
    }

    public PostState getState() {
        return state;
    }

    public void setState(PostState state) {
        this.state = state;
    }
    
    public List<MediaUploadDataResponse> getMediaUploads() {
        return mediaUploads;
    }

    public void setMediaUploads(List<MediaUploadDataResponse> mediaUploads) {
        this.mediaUploads = mediaUploads;
    }

    public static PostDataResponse convert(Post p) {
        PostDataResponse post = new PostDataResponse(p.getId(), p.getTitle(), p.getContent(), p.getCategory(), p.getCreationDate());
        UserDataResponse user = UserDataResponse.convert(p.getAuthor());
        post.setComments(CommentDataResponse.convert(p.getComments()));
        post.setState(p.getState());
        post.setLikes(p.getReactions().size());
        post.setMediaUploads(MediaUploadDataResponse.convert(p.getMediaUploads()));
        return post.setAuthor(user);
    }

    public static PostDataResponse convert(Post p, String AuthorName) {
        PostDataResponse post = new PostDataResponse(p.getId(), p.getTitle(), p.getContent(), p.getCategory(), p.getCreationDate());
        UserDataResponse user = UserDataResponse.convert(p.getAuthor());
        post.setComments(CommentDataResponse.convert(p.getComments(), AuthorName));
        post.setState(p.getState());
        post.setLikes(p.getReactions().size());
        post.setMediaUploads(MediaUploadDataResponse.convert(p.getMediaUploads()));
        return post.setAuthor(user);
    }
}


