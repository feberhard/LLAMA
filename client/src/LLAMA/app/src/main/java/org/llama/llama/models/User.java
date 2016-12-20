package org.llama.llama.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Felix on 22.11.2016.
 */
@IgnoreExtraProperties
public class User {

    public HashMap<String, Boolean> chats = new HashMap<>();
    public HashMap<String, Boolean> contacts = new HashMap<>();
    public String country;
    public String defaultLanguage;
    public String email;
    public String firebaseInstanceIdToken;
    public HashMap<String, Boolean> languages = new HashMap<>();
    public List<Double> location = new ArrayList<>();
    public String mood;
    public String name;
    public String username;

    public User(){

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
