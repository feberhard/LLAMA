package org.llama.llama.model;

/**
 * Created by woernsn on 05.12.16.
 */

public enum ChatType {
    DIALOG("dialog"), GROUP("group");

    private String text;

    ChatType(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }
}
