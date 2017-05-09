package com.sanislo.lostandfound.model;

/**
 * Created by root on 30.04.17.
 */

public class ChatMessage {
    private String authorUid;
    private String authorName;
    private String message;
    private boolean isReadByRecipient;
    private long timestamp;

    public ChatMessage() {
    }

    public ChatMessage(String authorUid, String authorName, String message, boolean isReadByRecipient, long timestamp) {
        this.authorUid = authorUid;
        this.authorName = authorName;
        this.message = message;
        this.isReadByRecipient = isReadByRecipient;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAuthorUid() {
        return authorUid;
    }

    public void setAuthorUid(String authorUid) {
        this.authorUid = authorUid;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isMine(String uid) {
        return authorUid.equals(uid);
    }

    public boolean isReadByRecipient() {
        return isReadByRecipient;
    }

    public void setReadByRecipient(boolean readByRecipient) {
        isReadByRecipient = readByRecipient;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "authorUid='" + authorUid + '\'' +
                ", authorName='" + authorName + '\'' +
                ", message='" + message + '\'' +
                ", isReadByRecipient=" + isReadByRecipient +
                ", timestamp=" + timestamp +
                '}';
    }
}
