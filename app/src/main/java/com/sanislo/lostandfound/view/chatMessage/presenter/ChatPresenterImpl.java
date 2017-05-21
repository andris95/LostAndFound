package com.sanislo.lostandfound.view.chatMessage.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sanislo.lostandfound.model.ChatHeader;
import com.sanislo.lostandfound.model.ChatMessage;
import com.sanislo.lostandfound.model.User;
import com.sanislo.lostandfound.model.api.ApiModel;
import com.sanislo.lostandfound.model.api.ApiModelImpl;
import com.sanislo.lostandfound.model.api.FirebaseEndPoint;
import com.sanislo.lostandfound.model.api.FirebaseEndPointImpl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 21.05.17.
 */

public class ChatPresenterImpl implements ChatPresenter {
    public static final String TAG = ChatPresenter.class.getSimpleName();
    public static final String LOREM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";

    private ChatView mChatView;

    private String mChatPartnerUid;
    private User mChatPartnerUser;

    private User mUser;
    private String mUid;

    private ChatMessage mChatMessage;
    private DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseEndPoint mFirebaseEndPoint = new FirebaseEndPointImpl();

    public ChatPresenterImpl(ChatView chatView, String chatPartnerUid) {
        mChatView = chatView;
        mUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mChatPartnerUid = chatPartnerUid;
        getUser();
    }

    private void getUser() {
        ApiModel apiModel = new ApiModelImpl();
        Call<List<User>> userCall = apiModel.getUserListByUID(mUid);
        userCall.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<com.sanislo.lostandfound.model.User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    mUser = response.body().get(0);
                    Log.d(TAG, "onResponse: " + mUser);
                }
            }

            @Override
            public void onFailure(Call<List<com.sanislo.lostandfound.model.User>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void getChatPartnerUser() {
        ApiModel apiModel = new ApiModelImpl();
        Call<List<User>> call = apiModel.getUserListByUID(mChatPartnerUid);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    mChatPartnerUser = response.body().get(0);
                    sendMessage();
                } else {
                    Log.d(TAG, "onResponse: fail");
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public void sendMessage(String message) {
        Log.d(TAG, "onClickSendMessage: " + message);
        //TODO hardcode
        if (TextUtils.isEmpty(message)) {
            message = LOREM;
        }
        if (TextUtils.isEmpty(message) || mUser == null) return;
        mChatView.onMessageSent();
        createNewChatMessage(message);
        getChatPartnerUser();
    }

    private void createNewChatMessage(String message) {
        mChatMessage = new ChatMessage();
        mChatMessage.setAuthorName(mUser.getFullName());
        mChatMessage.setAuthorUid(getAuthenticatedUserUID());
        mChatMessage.setMessage(message);
        long timestamp = new Date().getTime();
        mChatMessage.setTimestamp(timestamp);
    }

    private void sendMessage() {
        Log.d(TAG, "sendMessage: " + mChatMessage);
        mDatabaseReference.updateChildren(getMessageUpdateMap(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Log.d(TAG, "onComplete: ");
                if (databaseError != null) {
                    databaseError.toException().printStackTrace();
                    mChatView.onError(databaseError.getMessage());
                }
            }
        });
    }

    private ChatHeader getMyChatHeader() {
        ChatHeader chatHeader = new ChatHeader(getAuthenticatedUserUID(),
                mUser.getFullName(),
                mUser.getAvatarURL(),
                mChatMessage.getMessage(),
                mChatPartnerUid,
                mChatPartnerUser.getFullName(),
                mChatPartnerUser.getAvatarURL(),
                mChatMessage.getTimestamp());
        return chatHeader;
    }

    private ChatHeader getPartnersChatHeader() {
        ChatHeader chatHeader = new ChatHeader(getAuthenticatedUserUID(),
                mUser.getFullName(),
                mUser.getAvatarURL(),
                mChatMessage.getMessage(),
                getAuthenticatedUserUID(),
                mUser.getFullName(),
                mUser.getAvatarURL(),
                mChatMessage.getTimestamp());
        return chatHeader;
    }

    private HashMap<String, Object> getMessageUpdateMap() {
        HashMap<String, Object> updateMap = new HashMap<>();

        String senderPath = mFirebaseEndPoint.getChatMessagePath(getAuthenticatedUserUID(),
                mChatPartnerUid,
                mChatMessage.getTimestamp());
        updateMap.put(senderPath, mChatMessage);
        String recepientPath = mFirebaseEndPoint.getChatMessagePath(mChatPartnerUid,
                getAuthenticatedUserUID(),
                mChatMessage.getTimestamp());
        updateMap.put(recepientPath, mChatMessage);
        updateMap.put(mFirebaseEndPoint.getChatHeaderPath(
                getAuthenticatedUserUID(),
                mChatPartnerUid
        ), getMyChatHeader());
        updateMap.put(mFirebaseEndPoint.getChatHeaderPath(
                mChatPartnerUid,
                getAuthenticatedUserUID()
        ), getPartnersChatHeader());
        return updateMap;
    }

    private String getAuthenticatedUserUID() {
        return mUid;
    }
}
