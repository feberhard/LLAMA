package org.llama.llama.services;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.llama.llama.model.User;

/**
 * Created by Felix on 21.11.2016.
 */

public class UserService implements IUserService {
    private static final String TAG = "UserService";

    @Override
    public String getCurrentUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public void getUserInfo(String userId, ValueEventListener vel){
        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference userRef = db.getReference().child("users").child(userId);
        userRef.addListenerForSingleValueEvent(vel);

    }
//    public User getUserInfo(String userId) {
//        final FirebaseDatabase database = FirebaseDatabase.getInstance();
//
//        DatabaseReference ref = database.getReference().child("users").child(userId);
//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                User user = dataSnapshot.getValue(User.class);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

    @Override
    public void updateFirebaseInstanceIdToken(String token) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference ref = database.getReference().child("users").child(getCurrentUserId()).child("firebaseInstanceIdToken");
        ref.setValue(token);
    }

    @Override
    public void updateProfile(User user){
        final FirebaseDatabase db = FirebaseDatabase.getInstance();

        DatabaseReference userRef = db.getReference().child("users").child(getCurrentUserId());
        userRef.child("defaultLanguage").setValue(user.getDefaultLanguage());
        userRef.child("mood").setValue(user.getMood());
        userRef.child("name").setValue(user.getName());

        // reset languages
        userRef.child("languages").setValue(null);

        // add all enabled languages
        for(String language : user.getLanguages()) {
            userRef.child("languages").child(language).setValue(true);
        }

    }
}
