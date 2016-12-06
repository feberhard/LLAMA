package org.llama.llama.model;

/**
 * Created by Felix on 5.12.2016.
 */

public class Message {
    private String language;
    private String message;
    private String user;

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

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
}
