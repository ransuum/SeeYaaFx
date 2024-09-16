package org.practice.seeyaa.models.dto;

import java.util.List;

public record UserWithLettersDto(
        String id,
        String email,
        String firstName,
        String lastName,
        String username,
        List<LetterDto> sendLetters,
        List<LetterDto> myLetters
) {
}
