package org.practice.seeyaa.util.choices_of_letters;

import javafx.scene.layout.VBox;
import org.practice.seeyaa.enums.TypeOfLetter;
import org.practice.seeyaa.models.dto.LetterDto;
import org.practice.seeyaa.service.UsersService;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class SentChoice implements Choice {
    private final UsersService usersService;

    public SentChoice(UsersService usersService) {
        this.usersService = usersService;
    }

    @Override
    public List<LetterDto> addToBox(int index, String email) {
        return usersService.findByEmail(email).sendLetters()
                .stream()
                .filter(letterDto -> letterDto.typeOfLetter().equals(org.practice.seeyaa.models.TypeOfLetter.LETTER))
                .sorted(Comparator.comparing(LetterDto::createdAt))
                .toList();
    }

    @Override
    public String getChoice() {
        return TypeOfLetter.SENT.getName();
    }
}
