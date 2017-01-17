package org.llama.llama.services;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import org.jdeferred.Deferred;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;
import org.llama.llama.MyApp;
import org.llama.llama.model.Country;
import org.llama.llama.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Felix on 21.11.2016.
 */

public class UserService implements IUserService {
    private static final String TAG = "UserService";
    private static Map<String, User> userCache = new HashMap<>();
    private static Map<String, Promise> userRequestCache = new HashMap<>();
    private static final String cacheFile = "UserServiceCache.bin";


    public UserService() {
        Map<String, User> userCache = StorageService.readFromFile(MyApp.getAppContext(), cacheFile);
        if (userCache != null) {
            UserService.userCache = userCache;
        }
    }

    @Override
    public String getCurrentUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public synchronized Promise getUserInfo(final String userId) {
        if (userRequestCache.containsKey(userId)) {
            return userRequestCache.get(userId);
        }

        final Deferred deferred = new DeferredObject();
        final Promise promise = deferred.promise();
        userRequestCache.put(userId, promise);

        User user = userCache.get(userId);
        if (user == null) {
            final FirebaseDatabase database = FirebaseDatabase.getInstance();

            DatabaseReference ref = database.getReference().child("users").child(userId);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    user.setId(userId);
//                    userCache.put(userId, user);
                    updateUserCache(user);
                    deferred.resolve(user);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    userRequestCache.remove(userId);
                    deferred.reject(null);
                }
            });
        } else {
            deferred.resolve(user);
        }

        return promise;
    }


    public synchronized void updateUserCache(User user) {
        userCache.put(user.getId(), user);
        // TODO probably make async call to storageService
        StorageService.writeToFile(MyApp.getAppContext(), cacheFile, userCache);
    }

    @Override
    public void updateFirebaseInstanceIdToken(String token) {
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            return;
        }
        
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

    private <T> String  updateUserProperty(String property, T value) {
        String userId = getCurrentUserId();
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(userId)
                .child(property)
                .setValue(value);
        return userId;
    }

    @Override
    public void updateCurrentUserDisplayName(String displayName) {
        User user = userCache.get(updateUserProperty("displayname", displayName));
        if (user != null)
            user.setName(displayName);
    }

    @Override
    public void updateCurrentUserMood(String mood) {
        User user = userCache.get(updateUserProperty("mood", mood));
        if (user != null)
            user.setMood(mood);
    }

    @Override
    public void updateCurrentUserEmail(String email) {
        User user = userCache.get(updateUserProperty("email", email));
        if (user != null)
            user.setEmail(email);
    }

    @Override
    public void updateCurrentUserCountry(String countryId) {
        User user = userCache.get(updateUserProperty("country", countryId));
        if (user != null)
            user.setCountry(countryId);
    }

    @Override
    public void updateCurrentUserDefaultLanguage(String defaultLanguageId) {
        User user = userCache.get(updateUserProperty("defaultLanguage", defaultLanguageId));
        if (user != null)
            user.setDefaultLanguage(defaultLanguageId);
    }

    @Override
    public void updateCurrentUserLanguages(final Set<String> languages) {
        Map<String, Object> langs = new HashMap<>();
        for (String lang : languages) {
            langs.put(lang, true);
        }

        User user = userCache.get(updateUserProperty("languages", langs));
        if (user != null)
            user.setLanguages(langs);
    }

    @Override
    public void updateCurrentUserNotifications(Boolean notifications) {
        User user = userCache.get(updateUserProperty("notifications", notifications));
        if (user != null)
            user.setNotifications(notifications);
    }
}