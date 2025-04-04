package org.practice.seeyaa.service.impl;

import jakarta.validation.Valid;
import org.practice.seeyaa.enums.TypeOfLetter;
import org.practice.seeyaa.models.dto.LetterDto;
import org.practice.seeyaa.models.dto.LetterWithAnswers;
import org.practice.seeyaa.models.entity.Users;
import org.practice.seeyaa.models.request.LetterRequest;
import org.practice.seeyaa.models.entity.Letter;
import org.practice.seeyaa.repo.LetterRepo;
import org.practice.seeyaa.repo.UsersRepo;
import org.practice.seeyaa.service.LetterService;
import org.practice.seeyaa.util.mappers.LetterMapper;
import org.practice.seeyaa.util.movedletterconf.MovedLetterConfiguration;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@Validated
public class LetterServiceImpl implements LetterService {
    private final LetterRepo letterRepo;
    private final UsersRepo usersRepo;
    private final MovedLetterConfiguration movedLetterConfiguration;

    public LetterServiceImpl(LetterRepo letterRepo, UsersRepo usersRepo, MovedLetterConfiguration movedLetterConfiguration) {
        this.letterRepo = letterRepo;
        this.usersRepo = usersRepo;
        this.movedLetterConfiguration = movedLetterConfiguration;
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ROLE_USER')")
    public Letter sendLetter(@Valid LetterRequest letterRequest) {
        Users usersBy = usersRepo.findByEmail(letterRequest.userBy())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Users usersTo = usersRepo.findByEmail(letterRequest.userTo())
                .orElseThrow(() -> new RuntimeException("User not found"));

        var letter = letterRepo.save(Letter.builder()
                .userBy(usersBy)
                .userTo(usersTo)
                .text(letterRequest.text())
                .topic(letterRequest.topic())
                .activeLetter(Boolean.TRUE)
                .createdAt(LocalDateTime.now())
                .build());

        usersBy.getSendLetters().add(letter);
        usersTo.getMyLetters().add(letter);

        return letter;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    public void setLetterToSpam(String letterId, String email) {
        movedLetterConfiguration.setLetterType(letterId, email, TypeOfLetter.SPAM);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    public void setLetterToGarbage(String letterId, String email) {
        movedLetterConfiguration.setLetterType(letterId, email, TypeOfLetter.GARBAGE);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    @Transactional
    public LetterWithAnswers findById(String id) {
        return LetterMapper.INSTANCE.toLetterWithAnswers(letterRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No letter found with id: " + id)));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    @Transactional
    public List<LetterDto> findAllSentByTopic(String topic, String userBy) {
        return LetterMapper.INSTANCE.letterListToLetterDtoList(
                letterRepo.findAllByTopicContainingAndUserBy(
                        topic,
                        usersRepo.findByEmail(userBy).orElse(null))
        );
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    @Transactional
    public List<LetterDto> findAllInboxByTopic(String topic, String userTo) {
        return LetterMapper.INSTANCE.letterListToLetterDtoList(letterRepo.findAllByTopicContainingAndUserTo(
                        topic,
                        usersRepo.findByEmail(userTo).orElse(null))
                .stream()
                .sorted(Comparator.comparing(Letter::getCreatedAt).reversed())
                .toList());
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    @Transactional
    public void deleteById(String id) {
        letterRepo.findById(id).ifPresent(letter1 -> {
            letter1.getUserBy().getSendLetters().remove(letter1);
            letter1.getUserTo().getMyLetters().remove(letter1);
            letterRepo.delete(letter1);
        });
    }
}
