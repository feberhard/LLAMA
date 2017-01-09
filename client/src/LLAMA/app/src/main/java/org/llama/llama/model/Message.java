package org.llama.llama.model;

import java.util.Map;

/**
 * Created by Felix on 5.12.2016.
 */

public class Message {
    public static final String TEXT = "text";
    public static final String MULTIMEDIA = "multimedia";

    private String language;
    private String message;
    private String user;
    private Long timestamp;
    private Map<String,String> translations;
    private String type; // text or multimedia

    private int viewType = -1;

    public Message() {
    }

    public Message(String language, String message, String user) {
        this.language = language;
        this.message = message;
        this.user = user;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }


    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, String> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<String, String> translations) {
        this.translations = translations;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
}
