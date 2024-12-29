package org.practice.seeyaa.util.choices_of_letters;

import org.practice.seeyaa.enums.TypeOfLetter;
import org.practice.seeyaa.models.dto.LetterDto;
import org.practice.seeyaa.service.impl.UsersServiceImpl;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class InboxesChoice implements Choice {

    private final UsersServiceImpl usersServiceImpl;

    public InboxesChoice(UsersServiceImpl usersServiceImpl) {
        this.usersServiceImpl = usersServiceImpl;
    }

    @Override
    public List<LetterDto> addToBox(int index, String email) {
        return usersServiceImpl.findByEmail(email).myLetters()
                .stream()
                .filter(letterDto -> letterDto.typeOfLetter().equals(org.practice.seeyaa.models.TypeOfLetter.LETTER))
                .sorted(Comparator.comparing(LetterDto::createdAt))
                .toList();
    }

    @Override
    public String getChoice() {
        return TypeOfLetter.INBOXES.getName();
    }
}
