package com.blogger._blog.details;

public class ProfileFollowStats {
    private Long followers;
    private Long following;
    public ProfileFollowStats() {
    }
    
    public ProfileFollowStats(Long followers,Long following) {
        this.followers = followers;
        this.following = following;
    }

    public Long getFollowers() {
        return this.followers;
    }

    public void setFollowers(Long value) {
        this.followers = value;
    }

    public Long getFollowing() {
        return this.following;
    }

    public void setFollowing(Long value) {
        this.following = value;
    }
}
