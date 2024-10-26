package org.practice.seeyaa.models.dto;

import java.time.LocalDateTime;

public record MessageDto(String id,
                         UsersDto users,
                         ChatDto chat,
                         LocalDateTime localDateTime) {
}
