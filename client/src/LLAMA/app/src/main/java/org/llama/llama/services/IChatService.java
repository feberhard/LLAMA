package org.llama.llama.services;

import org.llama.llama.model.Chat;

import java.util.List;

/**
 * Created by Felix on 21.11.2016.
 */

public interface IChatService {

    List<Chat> getAvailableChats();

    void getConversation(String chatId);

    String createChat();

    String createDialogChat(String partnerUsername);
}
