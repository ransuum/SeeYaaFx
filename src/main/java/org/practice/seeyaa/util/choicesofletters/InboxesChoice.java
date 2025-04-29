package org.practice.seeyaa.util.choicesofletters;

import org.practice.seeyaa.enums.TypeOfLetter;
import org.practice.seeyaa.models.dto.LetterDto;
import org.practice.seeyaa.service.UsersService;
import org.practice.seeyaa.service.impl.UsersServiceImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Component
public class InboxesChoice implements Choice {

    private final UsersService usersService;

    public InboxesChoice(UsersServiceImpl usersService) {
        this.usersService = usersService;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    @Transactional(readOnly = true)
    public List<LetterDto> addToBox(int index, String email) {
        return usersService.findByEmail()
                .myLetters()
                .stream()
                .filter(LetterDto::activeLetter)
                .sorted(Comparator.comparing(LetterDto::createdAt))
                .toList();
    }

    @Override
    public TypeOfLetter getChoice() {
        return TypeOfLetter.INBOXES;
    }
}
