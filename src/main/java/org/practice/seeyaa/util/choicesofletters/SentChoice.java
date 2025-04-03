package org.practice.seeyaa.util.choicesofletters;

import org.practice.seeyaa.enums.TypeOfLetter;
import org.practice.seeyaa.models.dto.LetterDto;
import org.practice.seeyaa.service.UsersService;
import org.practice.seeyaa.service.impl.UsersServiceImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class SentChoice implements Choice {
    private final UsersService usersService;

    public SentChoice(UsersServiceImpl usersService) {
        this.usersService = usersService;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<LetterDto> addToBox(int index, String email) {
        return usersService.findByEmail()
                .sendLetters()
                .stream()
                .filter(LetterDto::activeLetter)
                .sorted(Comparator.comparing(LetterDto::createdAt))
                .toList();
    }

    @Override
    public TypeOfLetter getChoice() {
        return TypeOfLetter.SENT;
    }
}
