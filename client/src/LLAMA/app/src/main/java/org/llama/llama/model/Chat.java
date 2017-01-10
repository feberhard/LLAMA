package org.llama.llama.model;

/**
 * Created by Felix on 28.11.2016.
 */

public class Chat {
    private String id;
    private String title;
    private String lastMessage;
    private String owner;
    private Long timestamp;
    private String type;

    public Chat() {
    }

    public Chat(String title) {
        this.title = title;
    }

    public Chat(String title, String lastMessage) {
        this.title = title;
        this.lastMessage = lastMessage;
    }

    //<editor-fold desc="Properties">

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    //</editor-fold>
}
