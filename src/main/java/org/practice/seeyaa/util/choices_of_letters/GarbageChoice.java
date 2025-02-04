package org.practice.seeyaa.util.choices_of_letters;

import org.practice.seeyaa.enums.TypeOfLetter;
import org.practice.seeyaa.models.dto.LetterDto;
import org.practice.seeyaa.service.LetterService;
import org.practice.seeyaa.service.impl.LetterServiceImpl;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class GarbageChoice implements Choice {
    private final LetterService letterService;

    public GarbageChoice(LetterServiceImpl letterService) {
        this.letterService = letterService;
    }

    @Override
    public List<LetterDto> addToBox(int index, String email) {
        return letterService.findAllByUserWithGarbageLetters(email)
                .stream()
                .sorted(Comparator.comparing(LetterDto::createdAt))
                .toList();
    }

    @Override
    public TypeOfLetter getChoice() {
        return TypeOfLetter.GARBAGE;
    }
}
