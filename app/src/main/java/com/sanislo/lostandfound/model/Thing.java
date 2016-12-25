package com.sanislo.lostandfound.model;

/**
 * Created by root on 24.12.16.
 */

public class Thing {
    private String key;
    private int type;
    private int category;
    private String userUID;
    private String title;
    private String description;
    private String photo;
    private long timestamp;

    public Thing() {}

    public Thing(String key, int type, int category, String userUID, String title, String description, String photo, long timestamp) {
        this.key = key;
        this.type = type;
        this.category = category;
        this.userUID = userUID;
        this.title = title;
        this.description = description;
        this.photo = photo;
        this.timestamp = timestamp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Thing{" +
                "key='" + key + '\'' +
                ", type=" + type +
                ", category='" + category + '\'' +
                ", userUID='" + userUID + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", photo='" + photo + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    public static class Builder {
        private String key;
        private int type;
        private String userUID;
        private String title;
        private String description;
        private int category;
        private String photo;
        private long timestamp;

        public Builder setKey(String key) {
            this.key = key;
            return this;
        }

        public Builder setType(int type) {
            this.type = type;
            return this;
        }

        public Builder setUserUID(String userUID) {
            this.userUID = userUID;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setCategory(int category) {
            this.category = category;
            return this;
        }

        public Builder setPhoto(String photo) {
            this.photo = photo;
            return this;
        }

        public Builder setTimestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Thing build() {
            return new Thing(key,
                    type,
                    category,
                    userUID,
                    title,
                    description,
                    photo,
                    timestamp);
        }
    }
}
