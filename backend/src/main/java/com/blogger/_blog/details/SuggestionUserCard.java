package com.blogger._blog.details;


public class SuggestionUserCard {

    private Long id;
    private String username;
    private String fullname;
    private boolean isSubscribedByMe;

    public SuggestionUserCard() {}

    public SuggestionUserCard(Long id,String username,String fullname, boolean isSubscribedByMe) {
        this.id=id;
        this.username=username;
        this.fullname=fullname;
        this.isSubscribedByMe=isSubscribedByMe;
    }

    public Long getId() {
        return this.id;
    }
    public String getUsername() {
        return this.username;
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

    public boolean getIsSubscribedByMe() {
        return this.isSubscribedByMe;
    }

    public void setIsSubscribedByMe(boolean value) {
        this.isSubscribedByMe = value;
    }
}