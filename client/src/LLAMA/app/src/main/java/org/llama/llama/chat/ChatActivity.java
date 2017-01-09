package org.llama.llama.chat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.llama.llama.MyApp;
import org.llama.llama.R;
import org.llama.llama.services.IChatService;
import org.llama.llama.services.IUserService;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ((MyApp) getApplication()).getServiceComponent().inject(this);

        Bundle b = getIntent().getExtras();
        chatId = null;
        if (b != null) {
            chatId = b.getString("chatId");
        }

        txtMessage = (EditText) findViewById(R.id.editTextNewMessage);
        findViewById(R.id.btn_send_message).setOnClickListener(this);

        RecyclerView recycler = (RecyclerView)findViewById(R.id.message_list_recycler);
        recycler.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
//        linearLayoutManager.setStackFromEnd(true);
        recycler.setLayoutManager(linearLayoutManager);

        ref = FirebaseDatabase.getInstance()
                .getReference()
                .child("messages")
                .child(chatId);
        query = ref
                .orderByChild("timestamp");

        mAdapter = new ChatAdapter(query, this.userService.getCurrentUserId(), this.userService);
        recycler.setAdapter(mAdapter);
    }

    private void sendMessage() {
        // TODO get message language from chat layout
        String messageLanguage = "de";
        String msg = txtMessage.getText().toString();
        String userId = this.userService.getCurrentUserId();
        String chatId = this.chatId;

        new SendMessageAsyncTask().execute(msg, messageLanguage, userId, chatId);

        txtMessage.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_send_message) {
            sendMessage();
        }
    }
}
