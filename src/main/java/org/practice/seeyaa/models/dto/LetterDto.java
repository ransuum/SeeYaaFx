package org.practice.seeyaa.models.dto;

import org.practice.seeyaa.models.enums.TypeOfLetter;

import java.time.LocalDateTime;

public record LetterDto(
        String id,
        String topic,
        String text,
        LocalDateTime createdAt,
        UsersDto userTo,
        UsersDto userBy,
        TypeOfLetter typeOfLetter) {
}
