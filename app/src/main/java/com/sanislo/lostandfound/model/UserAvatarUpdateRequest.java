package com.sanislo.lostandfound.model;

import com.google.gson.annotations.Expose;

/**
 * Created by root on 31.03.17.
 */

public class UserAvatarUpdateRequest {
    @Expose
    String avatarURL;

    public UserAvatarUpdateRequest(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }
}
