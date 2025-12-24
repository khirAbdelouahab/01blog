package com.blogger._blog.details;

public class OtherUserData {
    private Long id;
    private String username;
    private String fullname;
    private boolean isFollowedByMe;
    private ProfileFollowStats followStats;

    public OtherUserData() {
    }

    public OtherUserData(Long id, String username, String fullname, boolean isFollowedByMe, Long followers,
            Long following) {
        this.id = id;
        this.username = username;
        this.fullname = fullname;
        this.isFollowedByMe = isFollowedByMe;
        this.followStats = new ProfileFollowStats(followers, following);
    }

    public Long getId() {
        return this.id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String name) {
        this.username = name;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public boolean getIsFollowedByMe() {
        return this.isFollowedByMe;
    }

    public void setIsFollowedByMe(boolean value) {
        this.isFollowedByMe = value;
    }

    public ProfileFollowStats getFollowStats() {
        return this.followStats;
    }

    public void setFollowStats(ProfileFollowStats value) {
        this.followStats = value;
    }
}
