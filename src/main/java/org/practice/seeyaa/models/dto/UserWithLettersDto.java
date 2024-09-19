package org.practice.seeyaa.models.dto;

import java.util.List;

public record UserWithLettersDto(
        String id,
        String email,
        String firstname,
        String lastname,
        String username,
        List<LetterDto> sendLetters,
        List<LetterDto> myLetters
) {
}
