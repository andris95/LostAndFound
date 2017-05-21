package com.sanislo.lostandfound.view.chatMessage.presenter;

/**
 * Created by root on 21.05.17.
 */

public interface ChatView {
    void onMessageSent();
    void onError(String message);
}
