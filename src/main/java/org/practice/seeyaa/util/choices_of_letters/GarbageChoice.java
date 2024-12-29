package org.practice.seeyaa.util.choices_of_letters;

import org.practice.seeyaa.enums.TypeOfLetter;
import org.practice.seeyaa.models.dto.LetterDto;
import org.practice.seeyaa.service.impl.LetterServiceImpl;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class GarbageChoice implements Choice {
    private final LetterServiceImpl letterServiceImpl;

    public GarbageChoice(LetterServiceImpl letterServiceImpl) {
        this.letterServiceImpl = letterServiceImpl;
    }

    @Override
    public List<LetterDto> addToBox(int index, String email) {
        return letterServiceImpl.findAllByUserWithGarbageLetters(email)
                .stream()
                .sorted(Comparator.comparing(LetterDto::createdAt))
                .toList();
    }

    @Override
    public String getChoice() {
        return TypeOfLetter.GARBAGE.getName();
    }
}
