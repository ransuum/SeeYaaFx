package org.practice.seeyaa.models.dto;

import java.time.LocalDateTime;

public record AnswerDto(String id, UsersDto userByAnswered, String answerText, LetterDto currentLetter, LocalDateTime createdAt) {
}
