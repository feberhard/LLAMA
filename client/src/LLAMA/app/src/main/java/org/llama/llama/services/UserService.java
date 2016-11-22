package org.llama.llama.services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    public void getUserInfo(String userId){

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

//    public void updateProfile(String name, String photoUri){
//        // https://firebase.google.com/docs/auth/android/manage-users
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                .setDisplayName(name)
//                .setPhotoUri(Uri.parse(photoUri))
//                .build();
//
//        user.updateProfile(profileUpdates)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Log.d(TAG, "User profile updated.");
//                        }
//                    }
//                });
//    }
}
