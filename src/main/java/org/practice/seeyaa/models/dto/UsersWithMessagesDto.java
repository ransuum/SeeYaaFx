package org.practice.seeyaa.models.dto;

import java.util.List;

public record UsersWithMessagesDto(String id,
                                   String email,
                                   String firstname,
                                   String lastname,
                                   String username,
                                   List<MessageDto> messages) {
}
