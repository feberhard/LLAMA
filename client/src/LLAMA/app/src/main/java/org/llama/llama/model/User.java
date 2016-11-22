package org.llama.llama.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Felix on 22.11.2016.
 */
@IgnoreExtraProperties
public class User {

    //chats
    //contacts
    public String country;
    public String defaultLanguage;
    public String email;
    public String firebaseInstanceIdToken;
    //languages
    //location
    public String mood;
    public String name;
    public String username;

    public User(){

    }
}
