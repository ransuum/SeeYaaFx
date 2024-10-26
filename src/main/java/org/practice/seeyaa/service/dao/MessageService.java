package org.practice.seeyaa.service.dao;

import org.practice.seeyaa.models.dto.MessageDto;
import org.practice.seeyaa.models.request.MessageRequest;

public interface MessageService {
    MessageDto createMessage(MessageRequest messageRequest, String chatId, String email);
    void deleteMessage(String id);
    void updateMessage(String id, MessageRequest messageRequest);
}
