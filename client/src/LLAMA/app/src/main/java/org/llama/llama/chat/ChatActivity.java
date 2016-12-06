package org.llama.llama.chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.llama.llama.MyApp;
import org.llama.llama.R;
import org.llama.llama.model.Chat;
import org.llama.llama.model.Message;
import org.llama.llama.services.ChatService;
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
    DatabaseReference ref;
    EditText txtMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ((MyApp) getApplication()).getServiceComponent().inject(this);

//        Bundle b = getIntent().getExtras();
//        String chatId = null;
//        if (b != null) {
//            chatId = b.getString("chatId");
//        }
//
//        if(chatId != null){
//            chatService.getConversation(chatId);
//        }

        txtMessage = (EditText) findViewById(R.id.editTextNewMessage);
        findViewById(R.id.btn_send_message).setOnClickListener(this);

        RecyclerView recycler = (RecyclerView)findViewById(R.id.message_list_recycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        ref = FirebaseDatabase.getInstance()
                .getReference()
                .child("messages")
                .child("c1");

        mAdapter = new ChatAdapter(ref);
        recycler.setAdapter(mAdapter);
    }

    private void sendMessage() {
        ref.push().setValue(new Message("de", txtMessage.getText().toString(), this.userService.getCurrentUserId()));
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
