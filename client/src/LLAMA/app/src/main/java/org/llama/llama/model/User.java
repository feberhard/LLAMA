package org.llama.llama.model;

import android.location.Location;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Felix on 22.11.2016.
 */
@IgnoreExtraProperties
public class User {
    public String id;
    //chats
    //contacts
    private String country;
    private String defaultLanguage;
    private String email;
    private String firebaseInstanceIdToken;
    private List<String> languages;
    private Location location;
    private String mood;
    private String name;
    private String username;

    public User(){
        this.languages = new ArrayList<>();
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

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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
