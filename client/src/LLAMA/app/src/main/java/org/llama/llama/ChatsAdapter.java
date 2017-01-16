//package org.llama.llama;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import org.jdeferred.DoneCallback;
//import org.jdeferred.Promise;
//import org.llama.llama.model.Chat;
//import org.llama.llama.model.User;
//import org.llama.llama.services.IUserService;
//
//import java.util.Collection;
//import java.util.List;
//
///**
// * Created by Felix on 28.11.2016.
// */
//
//public class ChatsAdapter extends ArrayAdapter<Chat> {
//    private int resource;
//    private String userId;
//    private IUserService userService;
//
//    public ChatsAdapter(Context context, int resource, List<Chat> objects, String userId, IUserService userService) {
//        super(context, resource, objects);
//        this.resource = resource;
//        this.userId = userId;
//        this.userService = userService;
//    }
//
//
//    //http://www.josecgomez.com/2010/05/03/android-putting-custom-objects-in-listview/
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        LinearLayout chatItem;
//        //Get the current alert object
//        Chat chat = getItem(position);
//
//        //Inflate the view
//        if (convertView == null) {
//            chatItem = new LinearLayout(getContext());
//            String inflater = Context.LAYOUT_INFLATER_SERVICE;
//            LayoutInflater vi;
//            vi = (LayoutInflater) getContext().getSystemService(inflater);
//            vi.inflate(resource, chatItem, true);
//        } else {
//            chatItem = (LinearLayout) convertView;
//        }
//        //Get the text boxes from the listitem.xml file
//        final TextView chatName = (TextView) chatItem.findViewById(R.id.txtChatName);
//        TextView lastMessage = (TextView) chatItem.findViewById(R.id.txtLastMessage);
//        ImageView chatImage = (ImageView) chatItem.findViewById(R.id.imgChatOverview);
//
//        //Assign the appropriate data from our alert object above
//        lastMessage.setText(chat.getLastMessage());
//        if (chat.getType().equals("group")) { // group
//            chatName.setText(chat.getTitle());
//            chatImage.setImageResource(R.drawable.ic_group);
//        } else if (chat.getType().equals("dialog")) { // dialog
//            chatImage.setImageResource(R.drawable.ic_person);
//
//            final FirebaseDatabase database = FirebaseDatabase.getInstance();
//            DatabaseReference ref = database.getReference().child("members").child(chat.getId());
//            ref.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
//                        String asdf = messageSnapshot.getKey();
//                        String childUserId = messageSnapshot.getKey();
//                        if (!childUserId.equals(userId)) {
//                            Promise p = userService.getUserInfo(childUserId);
//                            p.done(new DoneCallback() {
//                                @Override
//                                public void onDone(Object result) {
//                                    User user = (User) result;
//                                    chatName.setText(user.getName());
//                                }
//                            });
//                            break;
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//        }
//
//        return chatItem;
//    }
//}
