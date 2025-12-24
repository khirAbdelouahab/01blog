package com.blogger._blog.details;

import java.util.ArrayList;
import java.util.List;

public class UserProfileDataResponse {
    private UserDataResponse userDataResponse;
    private ProfileFollowStats profileFollowStats;
    private List<PostDataResponse> postsDataResponse;
    private boolean isConnectedUser;
    private boolean isSubscribedByMe;

    public UserProfileDataResponse() {}
    public UserProfileDataResponse(UserDataResponse userDataResponse,List<PostDataResponse> postsDataResponse) {
        this.postsDataResponse=postsDataResponse;
        this.userDataResponse=userDataResponse;
    }

    public UserProfileDataResponse(UserDataResponse userDataResponse) {
        this.postsDataResponse= new ArrayList<>();
        this.userDataResponse=userDataResponse;

    }

    public UserProfileDataResponse(List<PostDataResponse> postsDataResponse) {
        this.postsDataResponse= postsDataResponse;
    }

    public UserDataResponse getUserDataResponse() {
        return this.userDataResponse;
    }

    public void setUserDataResponse(UserDataResponse userDataResponse) {
        this.userDataResponse=userDataResponse;
    }

    public ProfileFollowStats getProfileFollowStats() {
        return this.profileFollowStats;
    }

    public void setProfileFollowStats(ProfileFollowStats value) {
        this.profileFollowStats = value;
    }
    public List<PostDataResponse> getPostDataResponse() {
        return this.postsDataResponse;
    }

    public void setPostDataResponse(List<PostDataResponse> postsDataResponse) {
        this.postsDataResponse=postsDataResponse;
    }

    public boolean getIsConnectedUser() {
        return this.isConnectedUser;
    }

    public void setIsConnectedUser(boolean value) {
        this.isConnectedUser = value;
    }


    public boolean getIsSubscribedByMe() {
        return this.isSubscribedByMe;
    }

    public void setIsSubscribedByMe(boolean value) {
        this.isSubscribedByMe = value;
    }
}
