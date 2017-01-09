package org.llama.llama.chat;

import android.view.View;

import org.llama.llama.model.Message;
import org.llama.llama.services.IUserService;

/**
 * Created by Felix on 9.1.2017.
 */

public class MultimediaViewHolder extends BaseViewHolder<Message, IUserService> {
    public MultimediaViewHolder(View view){
        super(view);
    }

    @Override
    public void setDataOnView(Message msg, IUserService userService) {

    }
}
