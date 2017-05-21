package com.sanislo.lostandfound.model.api;

import com.sanislo.lostandfound.utils.FirebaseConstants;

/**
 * Created by root on 21.05.17.
 */

public class FirebaseEndPointImpl implements FirebaseEndPoint {

    @Override
    public String getChatMessagePath(String userOneUid, String userTwoUid) {
        StringBuilder sb = new StringBuilder();
        sb.append(FirebaseConstants.CHAT_MESSAGES);
        sb.append("/");
        sb.append(userOneUid);
        sb.append("/");
        sb.append(userTwoUid);
        return sb.toString();
    }

    @Override
    public String getChatMessagePath(String userOneUid, String userTwoUid, long timestamp) {
        StringBuilder sb = new StringBuilder();
        sb.append(FirebaseConstants.CHAT_MESSAGES);
        sb.append("/");
        sb.append(userOneUid);
        sb.append("/");
        sb.append(userTwoUid);
        sb.append("/");
        sb.append(timestamp);
        return sb.toString();
    }

    @Override
    public String getChatHeaderPath(String userOneUid, String userTwoUid) {
        StringBuilder sb = new StringBuilder();
        sb.append(FirebaseConstants.CHAT_HEADERS);
        sb.append("/");
        sb.append(userOneUid);
        sb.append("/");
        sb.append(userTwoUid);
        return sb.toString();
    }
}
