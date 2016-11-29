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
import java.util.List;

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
        chats.add(new Chat("Werner van llama"));
        chats.add(new Chat("Sergeant llama"));
        chats.add(new Chat("Llama McLlamaface"));
        chats.add(new Chat("Kuzco"));
        chats.add(new Chat("Apollo 13", "ist ein kluges Tier"));

        return chats;
    }
}
