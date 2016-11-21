package org.llama.llama.services;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Felix on 21.11.2016.
 */

public class UserService implements IUserService {

    @Override
    public String getCurrentUserId(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public void getUserInfo(String userId){

    }
}
