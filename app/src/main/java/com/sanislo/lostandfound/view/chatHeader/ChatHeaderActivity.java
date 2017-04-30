package com.sanislo.lostandfound.view.chatHeader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.sanislo.lostandfound.R;
import com.sanislo.lostandfound.adapter.ChatHeaderAdapter;
import com.sanislo.lostandfound.adapter.ChatMessageAdapter;
import com.sanislo.lostandfound.model.ChatHeader;
import com.sanislo.lostandfound.model.ChatMessage;
import com.sanislo.lostandfound.utils.FirebaseConstants;
import com.sanislo.lostandfound.view.BaseActivity;
import com.sanislo.lostandfound.view.chatMessage.ChatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by root on 30.04.17.
 */

public class ChatHeaderActivity extends BaseActivity {
    public static final String TAG = ChatHeaderActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.rv_chat_headers)
    RecyclerView rvChatHeaders;

    private ChatHeaderAdapter mChatHeaderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_header);
        ButterKnife.bind(this);
        setupToolbar();
        setupChatHeadersAdapter();
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
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

    private void setupChatHeadersAdapter() {
        Query query = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseConstants.CHAT_HEADERS)
                .child(getAuthenticatedUserUID());
        mChatHeaderAdapter = new ChatHeaderAdapter(ChatHeader.class,
                R.layout.item_chat_header,
                ChatHeaderAdapter.ViewHolder.class,
                query);
        mChatHeaderAdapter.setOnClickListener(new ChatHeaderAdapter.OnClickListener() {
            @Override
            public void onClick(ChatHeader chatHeader) {
                Intent intent = new Intent(ChatHeaderActivity.this, ChatActivity.class);
                //TODO check recipient uid!!!
                intent.putExtra(ChatActivity.EXTRA_CHAT_PARTNER_UID, chatHeader.getRecipientUid());
                intent.putExtra(ChatActivity.EXTRA_CHAT_PARTNER_NAME, chatHeader.getRecipientName());
                intent.putExtra(ChatActivity.EXTRA_CHAT_PARTNER_AVATAR_URL, chatHeader.getRecipientAvatarUrl());
                startActivity(intent);
            }
        });
        rvChatHeaders.setLayoutManager(new LinearLayoutManager(ChatHeaderActivity.this));
        rvChatHeaders.setAdapter(mChatHeaderAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mChatHeaderAdapter.cleanup();
    }
}
