package com.blogger._blog.details;

public class PostDataResponseView {
    private PostDataResponse post;
    private boolean isLikedByMe;

    public PostDataResponseView() {}

    public PostDataResponseView(PostDataResponse post,boolean isLikedByMe) {
        this.post = post;
        this.isLikedByMe = isLikedByMe;
    }

    public PostDataResponse getPost() {
        return this.post;
    }
    

    public void setPost(PostDataResponse post) {
        this.post = post;
    }

    public boolean getIsLikedByMe() {
        return this.isLikedByMe;
    }

    public void setIsLikedByMe(boolean value) {
        this.isLikedByMe = value;
    }
}
