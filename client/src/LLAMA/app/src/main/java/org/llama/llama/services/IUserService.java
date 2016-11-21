package org.llama.llama.services;

/**
 * Created by Felix on 21.11.2016.
 */

public interface IUserService {
    String getCurrentUserId();

    void getUserInfo(String userId);
}
