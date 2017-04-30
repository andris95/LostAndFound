package com.sanislo.lostandfound.model;

/**
 * Created by root on 30.04.17.
 */

public class ChatHeader {
    private String lastMessageAuthorUid;
    private String lastMessageAuthorName;
    private String lastMessageAuthorAvatarUrl;
    private String message;
    private String recipientUid;
    private String recipientName;
    private String recipientAvatarUrl;
    private long timestamp;

    public ChatHeader() {
    }

    public ChatHeader(String lastMessageAuthorUid, String lastMessageAuthorName, String lastMessageAuthorAvatarUrl, String message, String recipientUid, String recipientName, String recipientAvatarUrl, long timestamp) {
        this.lastMessageAuthorUid = lastMessageAuthorUid;
        this.lastMessageAuthorName = lastMessageAuthorName;
        this.lastMessageAuthorAvatarUrl = lastMessageAuthorAvatarUrl;
        this.message = message;
        this.recipientUid = recipientUid;
        this.recipientName = recipientName;
        this.recipientAvatarUrl = recipientAvatarUrl;
        this.timestamp = timestamp;
    }

    public String getLastMessageAuthorUid() {
        return lastMessageAuthorUid;
    }

    public void setLastMessageAuthorUid(String lastMessageAuthorUid) {
        this.lastMessageAuthorUid = lastMessageAuthorUid;
    }

    public String getLastMessageAuthorName() {
        return lastMessageAuthorName;
    }

    public void setLastMessageAuthorName(String lastMessageAuthorName) {
        this.lastMessageAuthorName = lastMessageAuthorName;
    }

    public String getLastMessageAuthorAvatarUrl() {
        return lastMessageAuthorAvatarUrl;
    }

    public void setLastMessageAuthorAvatarUrl(String lastMessageAuthorAvatarUrl) {
        this.lastMessageAuthorAvatarUrl = lastMessageAuthorAvatarUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRecipientUid() {
        return recipientUid;
    }

    public void setRecipientUid(String recipientUid) {
        this.recipientUid = recipientUid;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientAvatarUrl() {
        return recipientAvatarUrl;
    }

    public void setRecipientAvatarUrl(String recipientAvatarUrl) {
        this.recipientAvatarUrl = recipientAvatarUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isLastMessageMine(String uid) {
        return lastMessageAuthorUid.equals(uid);
    }

    @Override
    public String toString() {
        return "ChatHeader{" +
                "lastMessageAuthorUid='" + lastMessageAuthorUid + '\'' +
                ", lastMessageAuthorName='" + lastMessageAuthorName + '\'' +
                ", lastMessageAuthorAvatarUrl='" + lastMessageAuthorAvatarUrl + '\'' +
                ", message='" + message + '\'' +
                ", recipientUid='" + recipientUid + '\'' +
                ", recipientName='" + recipientName + '\'' +
                ", recipientAvatarUrl='" + recipientAvatarUrl + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
