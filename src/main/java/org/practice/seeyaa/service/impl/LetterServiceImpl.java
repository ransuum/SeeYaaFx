package org.practice.seeyaa.service.impl;

import jakarta.validation.Valid;
import org.practice.seeyaa.enums.TypeOfLetter;
import org.practice.seeyaa.exception.NotFoundException;
import org.practice.seeyaa.models.dto.LetterDto;
import org.practice.seeyaa.models.dto.LetterWithAnswers;
import org.practice.seeyaa.models.entity.Users;
import org.practice.seeyaa.models.request.LetterRequestDto;
import org.practice.seeyaa.models.entity.Letter;
import org.practice.seeyaa.repo.LetterRepo;
import org.practice.seeyaa.repo.UsersRepo;
import org.practice.seeyaa.service.LetterService;
import org.practice.seeyaa.mappers.LetterMapper;
import org.practice.seeyaa.configuration.movedletterconf.MovedLetterConfiguration;
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
    public LetterDto sendLetter(@Valid LetterRequestDto letterRequestDto) {
        final Users usersBy = usersRepo.findByEmail(letterRequestDto.userBy())
                .orElseThrow(() -> new NotFoundException("User not found"));
        return usersRepo.findByEmail(letterRequestDto.userTo())
                .map(userTo -> LetterMapper.INSTANCE.toLetterDto(letterRepo.save(
                        Letter.builder()
                                .userBy(usersBy)
                                .userTo(userTo)
                                .text(letterRequestDto.text())
                                .topic(letterRequestDto.topic())
                                .activeLetter(Boolean.TRUE)
                                .createdAt(LocalDateTime.now())
                                .build()))
                ).orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    @Transactional
    public void setLetterToSpam(String letterId, String email) {
        movedLetterConfiguration.setLetterType(letterId, email, TypeOfLetter.SPAM);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    @Transactional
    public void setLetterToGarbage(String letterId, String email) {
        movedLetterConfiguration.setLetterType(letterId, email, TypeOfLetter.GARBAGE);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    @Transactional(readOnly = true)
    public LetterWithAnswers findById(String id) {
        return LetterMapper.INSTANCE.toLetterWithAnswers(letterRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("No letter found with id: " + id)));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    @Transactional(readOnly = true)
    public List<LetterDto> findAllSentByTopic(String topic, String userBy) {
        return LetterMapper.INSTANCE.letterListToLetterDtoList(
                letterRepo.findAllByTopicContainingAndUserBy(
                        topic,
                        usersRepo.findByEmail(userBy).orElse(null))
        );
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    @Transactional(readOnly = true)
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
        letterRepo.findById(id).ifPresent(letterRepo::delete);
    }
}
