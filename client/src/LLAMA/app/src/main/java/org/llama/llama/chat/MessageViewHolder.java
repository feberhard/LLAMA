package org.llama.llama.chat;

import android.view.View;
import android.widget.TextView;

import org.jdeferred.DoneCallback;
import org.llama.llama.R;
import org.llama.llama.model.Message;
import org.llama.llama.model.User;
import org.llama.llama.services.IUserService;

/**
 * Created by Felix on 6.12.2016.
 */

public class MessageViewHolder extends BaseViewHolder<Message, IUserService> {
    public final TextView txtUsername;
    public final TextView txtMessage;

    public MessageViewHolder(View view){
        super(view);

        txtUsername = (TextView)view.findViewById(R.id.message_username);
        txtMessage = (TextView)view.findViewById(R.id.message_text);
    }

    @Override
    public void setDataOnView(Message msg, IUserService userService) {
        txtMessage.setText(msg.getMessage());
//        txtUsername.setText(msg.getUser());

        userService.getUserInfo(msg.getUser()).done(new DoneCallback() {
            @Override
            public void onDone(Object result) {
                txtUsername.setText(((User)result).getName());
            }
        });
//        GetUserInfoAsyncTask task = new GetUserInfoAsyncTask(msg.getUser(), this.txtUsername, userService);
//        task.execute();
    }
}
