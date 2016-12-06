package org.llama.llama.chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Felix on 6.12.2016.
 */

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder{
    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void setDataOnView(T data);
}
