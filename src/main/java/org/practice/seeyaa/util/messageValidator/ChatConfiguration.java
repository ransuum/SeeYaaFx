package org.practice.seeyaa.util.messageValidator;

import org.practice.seeyaa.models.entity.Chat;
import org.practice.seeyaa.models.entity.Message;
import org.practice.seeyaa.models.entity.Users;
import org.springframework.stereotype.Component;

@Component
public class ChatConfiguration {

    public Chat checkForDoubleChat(Users by, Users to){
        Chat chat = null;

        for (Message message1 : by.getMessages()){
            for (Users user : message1.getChat().getParticipants()){
                if (user.equals(to)) {
                    chat = message1.getChat();
                    break;
                }
            }
        }
        return chat;
    }
}
