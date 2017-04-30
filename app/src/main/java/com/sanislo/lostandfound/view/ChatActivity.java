package com.sanislo.lostandfound.view;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.sanislo.lostandfound.R;
import com.sanislo.lostandfound.adapter.ChatMessageAdapter;
import com.sanislo.lostandfound.model.ChatMessage;
import com.sanislo.lostandfound.model.User;
import com.sanislo.lostandfound.model.api.ApiModel;
import com.sanislo.lostandfound.model.api.ApiModelImpl;
import com.sanislo.lostandfound.utils.FirebaseConstants;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 30.04.17.
 */

public class ChatActivity extends BaseActivity {
    public static final String TAG = ChatActivity.class.getSimpleName();
    public static final String EXTRA_CHAT_PARTNER_UID = "EXTRA_CHAT_PARTNER_UID";
    public static final String EXTRA_CHAT_PARTNER_NAME = "EXTRA_CHAT_PARTNER_NAME";
    public static final String EXTRA_CHAT_PARTNER_AVATAR_URL = "EXTRA_CHAT_PARTNER_AVATAR_URL";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.tv_user_name)
    TextView tvUserName;

    @BindView(R.id.iv_thing_author_avatar)
    ImageView ivChatPartnerAvatar;

    @BindView(R.id.edt_message)
    EditText edtMessage;

    @BindView(R.id.rv_chat)
    RecyclerView rvChat;

    private ChatMessageAdapter mChatMessageAdapter;
    private User mUser;
    private String mChatPartnerUid;
    private String mChatPartnerName;
    private String mChatPartnerAvatarUrl;
    private DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    //private ChatPresenter mChatPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        fetchIntent();
        getUser();
        setupToolbar();
        setupChatMessageAdapter();
        //mChatPresenter = new ChatPresenterImpl();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Log.d(TAG, "setupToolbar: " + mChatPartnerName);
        Log.d(TAG, "setupToolbar: " + mChatPartnerAvatarUrl);
        tvUserName.setText(mChatPartnerName);
        Glide.with(this)
                .load(mChatPartnerAvatarUrl)
                .error(R.drawable.account)
                .into(ivChatPartnerAvatar);
    }

    private void fetchIntent() {
        mChatPartnerUid = getIntent().getStringExtra(EXTRA_CHAT_PARTNER_UID);
        mChatPartnerName = getIntent().getStringExtra(EXTRA_CHAT_PARTNER_NAME);
        mChatPartnerAvatarUrl = getIntent().getStringExtra(EXTRA_CHAT_PARTNER_AVATAR_URL);
    }

    private void getUser() {
        ApiModel apiModel = new ApiModelImpl();
        Call<List<User>> userCall = apiModel.getUserListByUID(getAuthenticatedUserUID());
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

    private void setupChatMessageAdapter() {
        Query query = mDatabaseReference.child(FirebaseConstants.CHAT_MESSAGES)
                .child(getAuthenticatedUserUID())
                .child(mChatPartnerUid);
        mChatMessageAdapter = new ChatMessageAdapter(ChatMessage.class,
                R.layout.item_chat_message,
                ChatMessageAdapter.ViewHolder.class,
                query);
        rvChat.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        rvChat.setAdapter(mChatMessageAdapter);
    }

    @OnClick(R.id.iv_send)
    public void onClickSendMessage() {
        String message = edtMessage.getText().toString();
        Log.d(TAG, "onClickSendMessage: " + message);
        if (TextUtils.isEmpty(message) || mUser == null) return;

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setAuthorName(mUser.getFullName());
        chatMessage.setAuthorUid(getAuthenticatedUserUID());
        chatMessage.setMessage(message);
        long timestamp = new Date().getTime();
        chatMessage.setTimestamp(timestamp);

        sendMessage(chatMessage);
    }

    private void sendMessage(ChatMessage chatMessage) {
        Log.d(TAG, "sendMessage: " + chatMessage);
        HashMap<String, Object> updateMap = new HashMap<>();
        updateMap.put(FirebaseConstants.CHAT_MESSAGES
                + "/" + getAuthenticatedUserUID()
                + "/" + mChatPartnerUid
                + "/" + chatMessage.getTimestamp(), chatMessage);
        updateMap.put(FirebaseConstants.CHAT_MESSAGES
                + "/" + mChatPartnerUid
                + "/" + getAuthenticatedUserUID()
                + "/" + chatMessage.getTimestamp(), chatMessage);
        mDatabaseReference.updateChildren(updateMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Log.d(TAG, "onComplete: ");
                if (databaseError != null) {
                    databaseError.toException().printStackTrace();
                }
                edtMessage.setText("");
            }
        });
    }
}
