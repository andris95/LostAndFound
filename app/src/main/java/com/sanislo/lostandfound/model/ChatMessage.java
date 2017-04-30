package com.sanislo.lostandfound.model;

/**
 * Created by root on 30.04.17.
 */

public class ChatMessage {
    private String authorUid;
    private String authorName;
    private String message;
    private long timestamp;

    public ChatMessage() {
    }

    public ChatMessage(String authorUid, String authorName, String message, long timestamp) {
        this.authorUid = authorUid;
        this.authorName = authorName;
        this.message = message;
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

    @Override
    public String toString() {
        return "ChatMessage{" +
                "authorUid='" + authorUid + '\'' +
                ", authorName='" + authorName + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
