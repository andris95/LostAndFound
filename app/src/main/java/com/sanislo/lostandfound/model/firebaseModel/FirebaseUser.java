package com.sanislo.lostandfound.model.firebaseModel;

import java.util.HashMap;

/**
 * Created by root on 04.09.16.
 */
public class FirebaseUser {
    private String uid;
    private String firstName;
    private String lastName;
    private String fullName;
    private long birthDate;
    private boolean showBirthDate;
    private String city;
    private int gender;
    private String phoneNumber;
    private String linkInstagram;
    private String linkFacebook;
    private String linkTwitter;
    private String avatarURL;
    private String avatarBlurURL;
    private boolean isOnline;
    private long lastActiveTimestamp;
    private String emailAddress;
    private String websiteAddress;

    public FirebaseUser() {}

    public FirebaseUser(String uid, String firstName, String lastName, String fullName, long birthDate, boolean showBirthDate, String city, int gender, String phoneNumber, String linkInstagram, String linkFacebook, String linkTwitter, String avatarURL, String avatarBlurURL, boolean isOnline, long lastActiveTimestamp, String emailAddress, String websiteAddress) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.showBirthDate = showBirthDate;
        this.city = city;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.linkInstagram = linkInstagram;
        this.linkFacebook = linkFacebook;
        this.linkTwitter = linkTwitter;
        this.avatarURL = avatarURL;
        this.avatarBlurURL = avatarBlurURL;
        this.isOnline = isOnline;
        this.lastActiveTimestamp = lastActiveTimestamp;
        this.emailAddress = emailAddress;
        this.websiteAddress = websiteAddress;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public long getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(long birthDate) {
        this.birthDate = birthDate;
    }

    public boolean isShowBirthDate() {
        return showBirthDate;
    }

    public void setShowBirthDate(boolean showBirthDate) {
        this.showBirthDate = showBirthDate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLinkInstagram() {
        return linkInstagram;
    }

    public void setLinkInstagram(String linkInstagram) {
        this.linkInstagram = linkInstagram;
    }

    public String getLinkFacebook() {
        return linkFacebook;
    }

    public void setLinkFacebook(String linkFacebook) {
        this.linkFacebook = linkFacebook;
    }

    public String getLinkTwitter() {
        return linkTwitter;
    }

    public void setLinkTwitter(String linkTwitter) {
        this.linkTwitter = linkTwitter;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public String getAvatarBlurURL() {
        return avatarBlurURL;
    }

    public void setAvatarBlurURL(String avatarBlurURL) {
        this.avatarBlurURL = avatarBlurURL;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public long getLastActiveTimestamp() {
        return lastActiveTimestamp;
    }

    public void setLastActiveTimestamp(long lastActiveTimestamp) {
        this.lastActiveTimestamp = lastActiveTimestamp;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getWebsiteAddress() {
        return websiteAddress;
    }

    public void setWebsiteAddress(String websiteAddress) {
        this.websiteAddress = websiteAddress;
    }

    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("firstName", getFirstName());
        hashMap.put("lastName", getLastName());
        hashMap.put("fullName", getFullName());
        hashMap.put("emailAddress", getEmailAddress());
        return hashMap;
    }

    @Override
    public String toString() {
        return "FirebaseUser{" +
                "uid='" + uid + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", fullName='" + fullName + '\'' +
                ", birthDate=" + birthDate +
                ", showBirthDate=" + showBirthDate +
                ", city='" + city + '\'' +
                ", gender=" + gender +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", linkInstagram='" + linkInstagram + '\'' +
                ", linkFacebook='" + linkFacebook + '\'' +
                ", linkTwitter='" + linkTwitter + '\'' +
                ", avatarURL='" + avatarURL + '\'' +
                ", avatarBlurURL='" + avatarBlurURL + '\'' +
                ", isOnline=" + isOnline +
                ", lastActiveTimestamp=" + lastActiveTimestamp +
                ", emailAddress='" + emailAddress + '\'' +
                ", websiteAddress='" + websiteAddress + '\'' +
                '}';
    }

    public static class Builder {
        private String uid;
        private String firstName;
        private String lastName;
        private String fullName;
        private long birthDate;
        private boolean showBirthDate;
        private String city;
        private int gender;
        private String phoneNumber;
        private String linkInstagram;
        private String linkFacebook;
        private String linkTwitter;
        private String avatarURL;
        private String avatarBlurURL;
        private boolean isOnline;
        private long lastActiveTimestamp;

        private String emailAddress;
        private String websiteAddress;

        public Builder setUid(String uid) {
            this.uid = uid;
            return this;
        }

        public Builder setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder setFullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder setBirthDate(long birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public Builder setCity(String city) {
            this.city = city;
            return this;
        }

        public Builder setShowBirthDate(boolean showBirthDate) {
            this.showBirthDate = showBirthDate;
            return this;
        }

        public Builder setGender(int gender) {
            this.gender = gender;
            return this;
        }

        public Builder setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder setLinkInstagram(String linkInstagram) {
            this.linkInstagram = linkInstagram;
            return this;
        }

        public Builder setLinkFacebook(String linkFacebook) {
            this.linkFacebook = linkFacebook;
            return this;
        }

        public Builder setLinkTwitter(String linkTwitter) {
            this.linkTwitter = linkTwitter;
            return this;
        }

        public Builder setAvatarURL(String avatarURL) {
            this.avatarURL = avatarURL;
            return this;
        }

        public Builder setAvatarBlurURL(String avatarBlurURL) {
            this.avatarBlurURL = avatarBlurURL;
            return this;
        }

        public Builder setOnline(boolean online) {
            isOnline = online;
            return this;
        }

        public Builder setLastActiveTimestamp(long lastActiveTimestamp) {
            this.lastActiveTimestamp = lastActiveTimestamp;
            return this;
        }

        public Builder setEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
            return this;
        }

        public Builder setWebsiteAddress(String websiteAddress) {
            this.websiteAddress = websiteAddress;
            return this;
        }

        //Return the finally consrcuted FirebaseUser object
        public FirebaseUser build() {
            FirebaseUser user = new FirebaseUser(uid,
                    firstName,
                    lastName,
                    fullName,
                    birthDate,
                    showBirthDate,
                    city,
                    gender,
                    phoneNumber,
                    linkInstagram,
                    linkFacebook,
                    linkTwitter,
                    avatarURL,
                    avatarBlurURL,
                    isOnline,
                    lastActiveTimestamp,
                    emailAddress,
                    websiteAddress);
            return user;
        }
    }
}