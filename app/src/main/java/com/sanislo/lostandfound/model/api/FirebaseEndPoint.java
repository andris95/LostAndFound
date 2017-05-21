package com.sanislo.lostandfound.model.api;

/**
 * Created by root on 21.05.17.
 */

public interface FirebaseEndPoint {
    String getChatMessagePath(String userOneUid, String userTwoUid);
    String getChatMessagePath(String userOneUid,
                              String userTwoUid,
                              long timestamp);
    String getChatHeaderPath(String userOneUid, String userTwoUid);
}
