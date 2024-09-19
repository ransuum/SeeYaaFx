package org.practice.seeyaa.models.dto;


import java.time.LocalDateTime;
import java.util.List;

public record LetterWithAnswers (
        String id,
        String topic,
        String text,
        LocalDateTime createdAt,
        UsersDto userTo,
        UsersDto userBy,
        List<AnswerDto> answers) {
}
