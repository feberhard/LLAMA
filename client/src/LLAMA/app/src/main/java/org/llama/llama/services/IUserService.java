package org.llama.llama.services;

import com.google.firebase.database.ValueEventListener;

import org.llama.llama.model.User;

/**
 * Created by Felix on 21.11.2016.
 */

public interface IUserService {
    String getCurrentUserId();

    void getUserInfo(String userId, ValueEventListener vel);

    void updateFirebaseInstanceIdToken(String token);

    void updateProfile(User user);
}
