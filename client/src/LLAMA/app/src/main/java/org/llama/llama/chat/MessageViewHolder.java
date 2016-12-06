package org.llama.llama.chat;

import android.view.View;
import android.widget.TextView;

import org.llama.llama.R;
import org.llama.llama.model.Message;

/**
 * Created by Felix on 6.12.2016.
 */

public class MessageViewHolder extends BaseViewHolder<Message> {
    public final TextView txtUsername;
    public final TextView txtMessage;

    public MessageViewHolder(View view){
        super(view);

        txtUsername = (TextView)view.findViewById(R.id.message_username);
        txtMessage = (TextView)view.findViewById(R.id.message_text);
    }

    @Override
    public void setDataOnView(Message msg) {
        txtUsername.setText(msg.getUser());
        txtMessage.setText(msg.getMessage());
    }
}
