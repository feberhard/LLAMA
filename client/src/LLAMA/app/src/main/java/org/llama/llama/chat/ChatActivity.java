package org.llama.llama.chat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.llama.llama.MyApp;
import org.llama.llama.R;
import org.llama.llama.chat.chatinfo.ChatInfoActivity;
import org.llama.llama.model.User;
import org.llama.llama.services.IChatService;
import org.llama.llama.services.IUserService;
import org.llama.llama.utils.ViewTools;

import java.io.IOException;

import javax.inject.Inject;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ChatActivity";

    @Inject
    IChatService chatService;
    @Inject
    IUserService userService;

    ChatAdapter mAdapter;
    Query query;
    DatabaseReference ref;
    EditText txtMessage;

    private String chatId;
    private String chatTitle;
    private boolean isGroup;
    private User user;
    private Promise userPromise;

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ((MyApp) getApplication()).getServiceComponent().inject(this);

        actionBar = getSupportActionBar();

        final FirebaseDatabase db = FirebaseDatabase.getInstance();

        Bundle b = getIntent().getExtras();
        chatId = null;
        if (b != null) {
            chatId = b.getString("chatId");
            chatTitle = b.getString("chatTitle");
            isGroup = b.getBoolean("isGroup");
            actionBar.setTitle(chatTitle);
        }

        userPromise = userService.getUserInfo(userService.getCurrentUserId());

        // load chat partner's username, if it is a dialog
        if (!isGroup) {
            db.getReference().child("members").child(chatId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (!ds.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            db.getReference().child("users").child(ds.getKey()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    chatTitle = dataSnapshot.getValue(String.class);
                                    actionBar.setTitle(chatTitle);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        txtMessage = (EditText) findViewById(R.id.editTextNewMessage);
        findViewById(R.id.btn_send_message).setOnClickListener(this);

        final RecyclerView recycler = (RecyclerView) findViewById(R.id.message_list_recycler);
        recycler.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recycler.setLayoutManager(linearLayoutManager);

        ref = db.getReference()
                .child("messages")
                .child(chatId);
        query = ref
                .orderByChild("timestamp");


        userPromise.done(new DoneCallback() {
            @Override
            public void onDone(Object result) {
                user = (User) result;
                mAdapter = new ChatAdapter(query, user, userService);
                recycler.setAdapter(mAdapter);
            }
        });

        // Listen for title change events
        DatabaseReference titleRef = db.getReference().child("chats").child(chatId).child("title");
        titleRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatTitle = (String) dataSnapshot.getValue();
                actionBar.setTitle(chatTitle);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Listen for members change (subtitle)
        if (isGroup) {
            DatabaseReference usersRef = db.getReference().child("members").child(chatId);
            usersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    actionBar.setSubtitle(String.format("%d %s", dataSnapshot.getChildrenCount(), getString(R.string.members)));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        // Listen for click events on ActionBar Title
        ViewTools.findActionBarTitle(getWindow().getDecorView()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, ChatInfoActivity.class);
                Bundle b = new Bundle();
                b.putString("chatId", chatId);
                b.putString("chatTitle", chatTitle);
                b.putBoolean("isGroup", isGroup);
                intent.putExtras(b);
                startActivity(intent);
            }
        });
    }

    private void sendMessage() {
        if (this.user != null) {
            this.sendMessageLoaded();
        }

        userPromise.done(new DoneCallback() {
            @Override
            public void onDone(Object result) {
                user = (User) result;
                sendMessageLoaded();
            }
        });
    }

    private void sendMessageLoaded() {
        String messageLanguage = this.user.getDefaultLanguage();
        String msg = txtMessage.getText().toString();
        String userId = this.userService.getCurrentUserId();
        String chatId = this.chatId;

        new SendMessageAsyncTask().execute(msg, messageLanguage, userId, chatId);

        txtMessage.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_send_message) {
            sendMessage();
        }
    }
}
