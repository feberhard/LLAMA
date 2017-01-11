package org.llama.llama.services;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.llama.llama.model.Chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Felix on 21.11.2016.
 */

public class ChatService implements IChatService {
    private static final String TAG = "ChatService";


    public void write() {
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("countries");
//
//        myRef.setValue("asdf");
    }

    public void read() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("countries");

        DatabaseReference ref = database.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue(String.class);

                Object obj = dataSnapshot.getValue();
                Log.d(TAG, "Name is: " + name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Failed to read value.", databaseError.toException());
            }
        });


//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String value = dataSnapshot.getValue(String.class);
//                Log.d(TAG, "Value is: " + value);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d(TAG, "Failed to read value.", databaseError.toException());
//            }
//        });
    }

    @Override
    public List<Chat> getAvailableChats() {
        List<Chat> chats = new ArrayList<>();
        chats.add(new Chat("Werner van llama", "May the llama be with you"));
        chats.add(new Chat("Sergeant llama"));
        chats.add(new Chat("Llama McLlamaface"));
        chats.add(new Chat("Kuzco"));
        chats.add(new Chat("Apollo 13", "ist ein kluges Tier"));

        return chats;
    }

    @Override
    public void getConversation(String chatId) {

    }

    @Override
    public String createChat() {
        String ownerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference chatsReference = db.getReference().child("chats");
        String key = chatsReference.push().getKey();
        Long timestamp = System.currentTimeMillis();

        // create chat entry
        Chat c = new Chat();
        c.setOwner(ownerId);
        c.setTitle("New Group chat");
        c.setType("group");
        c.setTimestamp(timestamp);
        c.setLastMessage("");
        chatsReference.child(key).setValue(c);

        // create members entry
        Map<String, Long> chatMembers = new HashMap<>();
        chatMembers.put(ownerId, timestamp);
        db.getReference().child("members").child(key).setValue(chatMembers);

        // create chat entry in users
        db.getReference().child("users").child(ownerId).child("chats").child(key).setValue(true);

        return key;
    }
}
