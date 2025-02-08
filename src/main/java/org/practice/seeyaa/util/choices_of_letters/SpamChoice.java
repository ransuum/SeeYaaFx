package org.practice.seeyaa.util.choices_of_letters;

import org.practice.seeyaa.enums.TypeOfLetter;
import org.practice.seeyaa.models.dto.LetterDto;
import org.practice.seeyaa.models.dto.MovedLetterDto;
import org.practice.seeyaa.service.MovedLetterService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SpamChoice implements Choice {
    private final MovedLetterService movedLetterService;

    public SpamChoice(MovedLetterService movedLetterService) {
        this.movedLetterService = movedLetterService;
    }

    @Override
    public List<LetterDto> addToBox(int index, String email) {
        return movedLetterService.getLettersWithSpam(email)
                .stream()
                .map(MovedLetterDto::letter)
                .toList();
    }

    @Override
    public TypeOfLetter getChoice() {
        return TypeOfLetter.SPAM;
    }
}
