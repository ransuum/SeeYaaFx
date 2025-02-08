package org.practice.seeyaa.models.dto;

import java.time.LocalDateTime;

public record LetterDto(
        String id,
        String topic,
        String text,
        LocalDateTime createdAt,
        UsersDto userTo,
        UsersDto userBy,
        Boolean activeLetter) {
}
