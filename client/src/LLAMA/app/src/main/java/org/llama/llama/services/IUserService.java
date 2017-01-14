package org.llama.llama.services;

import org.jdeferred.Promise;
import org.llama.llama.model.Country;
import org.llama.llama.model.User;

import java.util.Set;
import java.util.concurrent.Future;

/**
 * Created by Felix on 21.11.2016.
 */

public interface IUserService {
    String getCurrentUserId();
    Promise getUserInfo(final String userId);
    void updateUserCache(User user);
    void updateFirebaseInstanceIdToken(String token);
    void updateCurrentUserDisplayName(String displayName);
    void updateCurrentUserMood(String mood);
    void updateCurrentUserEmail(String email);
    void updateCurrentUserCountry(String countryId);
    void updateCurrentUserLanguages(Set<String> languages);
    void updateCurrentUserDefaultLanguage(String defaultLanguageId);
}
