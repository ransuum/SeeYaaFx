package org.practice.seeyaa.service;

import org.practice.seeyaa.models.dto.ChatDto;
import org.practice.seeyaa.models.entity.Chat;
import org.practice.seeyaa.models.entity.Message;
import org.practice.seeyaa.models.entity.Users;
import org.practice.seeyaa.repo.ChatRepo;
import org.practice.seeyaa.repo.MessageRepo;
import org.practice.seeyaa.repo.UsersRepo;
import org.practice.seeyaa.service.dao.ChatService;
import org.practice.seeyaa.util.mappers.Mapper;
import org.practice.seeyaa.util.messageValidator.ChatConfiguration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {
    private final ChatRepo chatRepo;
    private final MessageRepo messageRepo;
    private final UsersRepo usersRepo;
    private final ChatConfiguration chatConfiguration;

    public ChatServiceImpl(ChatRepo chatRepo, MessageRepo messageRepo, UsersRepo usersRepo, ChatConfiguration chatConfiguration) {
        this.chatRepo = chatRepo;
        this.messageRepo = messageRepo;
        this.usersRepo = usersRepo;
        this.chatConfiguration = chatConfiguration;
    }

    @Override
    public ChatDto createChat(String byUser, String message, String toUser) {
        Users by = usersRepo.findByEmail(byUser).orElseThrow(() -> new RuntimeException("App Error"));
        Users to = usersRepo.findByEmail(toUser).orElseThrow(() -> new RuntimeException("Not found"));

        Chat chat = this.chatConfiguration.checkForDoubleChat(by, to);

        if (chat != null) {
            Set<Users> participants = new HashSet<>();
            participants.add(by);
            participants.add(to);

            chat = Chat.builder()
                    .participants(participants)
                    .build();
            chatRepo.save(chat);
        }

        Message message1 = Message.builder()
                .chat(chat)
                .text(message)
                .users(by)
                .localDateTime(LocalDateTime.now())
                .build();
        messageRepo.save(message1);

        return Mapper.INSTANCE.toChatDto(chat);
    }

    @Override
    @Transactional
    public void deleteChat(String chatId) {
        chatRepo.deleteById(chatId);
    }

    @Override
    public List<ChatDto> findAll(String by) {
        return chatRepo.findAllUserChatsJPQL(by)
                .stream()
                .map(Mapper.INSTANCE::toChatDto)
                .collect(Collectors.toList());
    }

}
