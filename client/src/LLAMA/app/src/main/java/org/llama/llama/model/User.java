package org.llama.llama.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Felix on 22.11.2016.
 */
@IgnoreExtraProperties
public class User {
    public String id;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirebaseInstanceIdToken() {
        return firebaseInstanceIdToken;
    }

    public void setFirebaseInstanceIdToken(String firebaseInstanceIdToken) {
        this.firebaseInstanceIdToken = firebaseInstanceIdToken;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
