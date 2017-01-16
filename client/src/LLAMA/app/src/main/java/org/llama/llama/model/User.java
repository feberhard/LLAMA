package org.llama.llama.model;

import android.location.Location;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Felix on 22.11.2016.
 */
@IgnoreExtraProperties
public class User {
    public String id;
    private Map<String, Object> chats = new HashMap<>();
    private Map<String, Object> contacts = new HashMap<>();
    private String country;
    private String defaultLanguage;
    private String email;
    private String firebaseInstanceIdToken;
    private Map<String, Object> languages = new HashMap<>();
    private List<Object> location = new ArrayList<>();
    private String mood;
    private String name;
    private String username;
    private Boolean notifications;

    public Map<String, Object> getChats() {
        return chats;
    }

    public void setChats(Map<String, Object> chats) {
        this.chats = chats;
    }

    public Map<String, Object> getContacts() {
        return contacts;
    }

    public void setContacts(Map<String, Object> contacts) {
        this.contacts = contacts;
    }

    public Map<String, Object> getLanguages() {
        return languages;
    }

    public void setLanguages(Map<String, Object> languages) {
        this.languages = languages;
    }

    public List<Object> getLocation() {
        return location;
    }

    public void setLocation(List<Object> location) {
        this.location = location;
    }

    public void setLocation(Double latitude, Double longitude) {
        this.location = Arrays.asList((Object)(new Double(latitude)), new Double(longitude));
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

    public Boolean getNotifications() {
        return notifications;
    }

    public void setNotifications(Boolean notifications) {
        this.notifications = notifications;
    }
}
