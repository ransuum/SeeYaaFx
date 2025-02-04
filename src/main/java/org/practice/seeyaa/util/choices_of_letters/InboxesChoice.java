package org.practice.seeyaa.util.choices_of_letters;

import org.practice.seeyaa.enums.TypeOfLetter;
import org.practice.seeyaa.models.dto.LetterDto;
import org.practice.seeyaa.service.UsersService;
import org.practice.seeyaa.service.impl.UsersServiceImpl;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class InboxesChoice implements Choice {

    private final UsersService usersService;

    public InboxesChoice(UsersServiceImpl usersService) {
        this.usersService = usersService;
    }

    @Override
    public List<LetterDto> addToBox(int index, String email) {
        return usersService.findByEmail(email).myLetters()
                .stream()
                .filter(letterDto -> letterDto.typeOfLetter().equals(TypeOfLetter.LETTER))
                .sorted(Comparator.comparing(LetterDto::createdAt))
                .toList();
    }

    @Override
    public TypeOfLetter getChoice() {
        return TypeOfLetter.INBOXES;
    }
}
