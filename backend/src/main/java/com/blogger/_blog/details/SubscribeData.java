package com.blogger._blog.details;

public class SubscribeData {
    private String senderName;
    private String receiverName;
    private boolean isFollower;
    public SubscribeData() {}
    public SubscribeData(String senderName, String receiverName, boolean isFollower) {
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.isFollower = isFollower;
    }
    public String getSenderName() {
        return this.senderName;
    }
    public void setSenderName(String name) {
        this.senderName = name;
    }
    public String getReceiverName() {
        return this.receiverName;
    }

    public void setReceiverName(String name) {
        this.receiverName = name;
    }

    public boolean getIsFollower() {
        return this.isFollower;
    }

    public void setIsFollower(boolean value) {
        this.isFollower = value;
    }
}
