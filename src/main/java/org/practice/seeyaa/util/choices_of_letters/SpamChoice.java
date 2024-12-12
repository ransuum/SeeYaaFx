package org.practice.seeyaa.util.choices_of_letters;

import javafx.scene.layout.VBox;
import org.practice.seeyaa.enums.TypeOfLetter;
import org.practice.seeyaa.models.dto.LetterDto;
import org.practice.seeyaa.service.LetterService;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class SpamChoice implements Choice {
    private final LetterService letterService;

    public SpamChoice(LetterService letterService) {
        this.letterService = letterService;
    }

    @Override
    public List<LetterDto> addToBox(int index, String email) {
        return letterService.findAllByUserWithSpamLetters(email)
                .stream()
                .sorted(Comparator.comparing(LetterDto::createdAt))
                .toList();
    }

    @Override
    public String getChoice() {
        return TypeOfLetter.SPAM.getName();
    }
}
