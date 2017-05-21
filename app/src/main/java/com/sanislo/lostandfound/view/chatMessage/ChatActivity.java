package com.sanislo.lostandfound.view.chatMessage;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.sanislo.lostandfound.R;
import com.sanislo.lostandfound.model.ChatMessage;
import com.sanislo.lostandfound.utils.FirebaseConstants;
import com.sanislo.lostandfound.view.BaseActivity;
import com.sanislo.lostandfound.view.chatMessage.adapter.ChatMessageAdapter;
import com.sanislo.lostandfound.view.chatMessage.presenter.ChatPresenter;
import com.sanislo.lostandfound.view.chatMessage.presenter.ChatPresenterImpl;
import com.sanislo.lostandfound.view.chatMessage.presenter.ChatView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

/**
 * Created by root on 30.04.17.
 */

public class ChatActivity extends BaseActivity implements ChatView {
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
    private String mChatPartnerUid;
    private String mChatPartnerName;
    private String mChatPartnerAvatarUrl;
    private DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    private ChatPresenter mChatPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        fetchIntent();
        setupToolbar();
        setupChatMessageAdapter();

        mChatPresenter = new ChatPresenterImpl(this, mChatPartnerUid);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: " + item.getItemId());
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
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left_white);
        tvUserName.setText(mChatPartnerName);
        Glide.with(this)
                .load(mChatPartnerAvatarUrl)
                .into(ivChatPartnerAvatar);
    }

    private void fetchIntent() {
        mChatPartnerUid = getIntent().getStringExtra(EXTRA_CHAT_PARTNER_UID);
        mChatPartnerName = getIntent().getStringExtra(EXTRA_CHAT_PARTNER_NAME);
        mChatPartnerAvatarUrl = getIntent().getStringExtra(EXTRA_CHAT_PARTNER_AVATAR_URL);
    }

    private LinearLayoutManager mLinearLayoutManager;
    private void setupChatMessageAdapter() {
        Query query = mDatabaseReference.child(FirebaseConstants.CHAT_MESSAGES)
                .child(getAuthenticatedUserUID())
                .child(mChatPartnerUid);
        mChatMessageAdapter = new ChatMessageAdapter(ChatMessage.class,
                R.layout.item_chat_message_left,
                ChatMessageAdapter.ViewHolder.class,
                query);
        mChatMessageAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int messageCount = mChatMessageAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (messageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    rvChat.scrollToPosition(positionStart);
                }
            }
        });
        mLinearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        rvChat.setLayoutManager(mLinearLayoutManager);
        rvChat.setItemAnimator(new SlideInUpAnimator(new OvershootInterpolator(2f)));
        rvChat.setAdapter(mChatMessageAdapter);
    }

    @OnClick(R.id.iv_send)
    public void onClickSendMessage() {
        String message = edtMessage.getText().toString();
        mChatPresenter.sendMessage(message);
    }

    @Override
    public void onMessageSent() {
        edtMessage.setText("");
    }

    @Override
    public void onError(String message) {
        makeToast(message);
    }
}
