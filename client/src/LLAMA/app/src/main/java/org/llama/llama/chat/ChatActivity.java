package org.llama.llama.chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.llama.llama.MyApp;
import org.llama.llama.R;
import org.llama.llama.services.ChatService;
import org.llama.llama.services.IChatService;

import javax.inject.Inject;

public class ChatActivity extends AppCompatActivity {

    @Inject
    IChatService chatService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ((MyApp) getApplication()).getServiceComponent().inject(this);

        Bundle b = getIntent().getExtras();
        String chatId = null;
        if (b != null) {
            chatId = b.getString("chatId");
        }

        if(chatId != null){
            chatService.getConversation(chatId);
        }
    }
}
