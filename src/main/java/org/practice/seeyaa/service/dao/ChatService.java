package org.practice.seeyaa.service.dao;


import org.practice.seeyaa.models.dto.ChatDto;

import java.util.List;

public interface ChatService {
    ChatDto createChat(String byUser, String message, String toUser);
    void deleteChat(String chatId);
    List<ChatDto> findAll(String byUser);
}
