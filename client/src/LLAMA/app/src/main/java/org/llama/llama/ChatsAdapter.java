package org.llama.llama;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.llama.llama.model.Chat;

import java.util.List;

/**
 * Created by Felix on 28.11.2016.
 */

public class ChatsAdapter extends ArrayAdapter<Chat> {
    int resource;
    public ChatsAdapter(Context context, int resource, List<Chat> objects) {
        super(context, resource, objects);
        this.resource = resource;
    }


    //http://www.josecgomez.com/2010/05/03/android-putting-custom-objects-in-listview/
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LinearLayout chatItem;
        //Get the current alert object
        Chat chat = getItem(position);

        //Inflate the view
        if(convertView==null)
        {
            chatItem = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi;
            vi = (LayoutInflater)getContext().getSystemService(inflater);
            vi.inflate(resource, chatItem, true);
        }
        else
        {
            chatItem = (LinearLayout) convertView;
        }
        //Get the text boxes from the listitem.xml file
        TextView chatName =(TextView)chatItem.findViewById(R.id.txtChatName);
        TextView lastMessage =(TextView)chatItem.findViewById(R.id.txtLastMessage);
        ImageView chatImage = (ImageView)chatItem.findViewById(R.id.imgChatOverview);

        //Assign the appropriate data from our alert object above
        chatName.setText(chat.getTitle());
        lastMessage.setText(chat.getLastMessage());

        return chatItem;
    }
}
