package org.practice.seeyaa.service;

import org.practice.seeyaa.models.dto.MessageDto;
import org.practice.seeyaa.models.entity.Message;
import org.practice.seeyaa.models.request.MessageRequest;
import org.practice.seeyaa.repo.ChatRepo;
import org.practice.seeyaa.repo.MessageRepo;
import org.practice.seeyaa.repo.UsersRepo;
import org.practice.seeyaa.service.dao.MessageService;
import org.practice.seeyaa.util.Mapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MessageServiceImpl implements MessageService {
    private final MessageRepo messageRepo;
    private final ChatRepo chatRepo;
    private final UsersRepo usersRepo;

    public MessageServiceImpl(MessageRepo messageRepo, ChatRepo chatRepo, UsersRepo usersRepo) {
        this.messageRepo = messageRepo;
        this.chatRepo = chatRepo;
        this.usersRepo = usersRepo;
    }

    @Override
    public MessageDto createMessage(MessageRequest messageRequest, String chatId, String email) {
        return Mapper.INSTANCE.toMessageDto(messageRepo.save(Message.builder()
                .text(messageRequest.getMessage())
                .localDateTime(LocalDateTime.now())
                .chat(chatRepo.findById(chatId).get())
                .users(usersRepo.findByEmail(email).get())
                .build()));
    }

    @Override
    public void deleteMessage(String id) {
        messageRepo.deleteById(id);
    }

    @Override
    public void updateMessage(String id, MessageRequest messageRequest) {

    }
}
