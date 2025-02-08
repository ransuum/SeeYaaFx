package org.practice.seeyaa.service.impl;

import jakarta.validation.Valid;
import org.practice.seeyaa.enums.TypeOfLetter;
import org.practice.seeyaa.models.dto.LetterDto;
import org.practice.seeyaa.models.dto.LetterWithAnswers;
import org.practice.seeyaa.models.entity.MovedLetter;
import org.practice.seeyaa.models.entity.Users;
import org.practice.seeyaa.models.request.LetterRequest;
import org.practice.seeyaa.models.entity.Letter;
import org.practice.seeyaa.repo.LetterRepo;
import org.practice.seeyaa.repo.MovedLetterRepo;
import org.practice.seeyaa.repo.UsersRepo;
import org.practice.seeyaa.service.LetterService;
import org.practice.seeyaa.util.mappers.LetterMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
public class LetterServiceImpl implements LetterService {
    private final LetterRepo letterRepo;
    private final UsersRepo usersRepo;
    private final MovedLetterRepo movedLetterRepo;

    public LetterServiceImpl(LetterRepo letterRepo, UsersRepo usersRepo, MovedLetterRepo movedLetterRepo) {
        this.letterRepo = letterRepo;
        this.usersRepo = usersRepo;
        this.movedLetterRepo = movedLetterRepo;
    }

    @Override
    @Transactional
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
                .createdAt(LocalDateTime.now())
                .build()
        );

        usersBy.getSendLetters().add(letter);
        usersTo.getMyLetters().add(letter);

        return letter;
    }

    @Override
    public void setLetterToSpam(String letterId, String email) {
        letterRepo.findById(letterId).ifPresent(letter -> {
            movedLetterRepo.save(MovedLetter.builder()
                    .typeOfLetter(TypeOfLetter.SPAM)
                    .movedBy(usersRepo.findByEmail(email).get())
                    .letter(letterRepo.findById(letterId).get())
                    .build());
        });
    }

    @Override
    public void setLetterToGarbage(String letterId, String email) {
        letterRepo.findById(letterId).ifPresent(letter -> {
            movedLetterRepo.save(MovedLetter.builder()
                    .typeOfLetter(TypeOfLetter.GARBAGE)
                    .movedBy(usersRepo.findByEmail(email).get())
                    .letter(letterRepo.findById(letterId).get())
                    .willDeleteAt(LocalDateTime.now().plusDays(30))
                    .build());
        });
    }

    @Override
    @Transactional
    public LetterWithAnswers findById(String id) {
        return LetterMapper.INSTANCE.toLetterWithAnswers(letterRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No letter found with id: " + id)));
    }

    @Override
    @Transactional
    public List<LetterDto> findAllByUserWithGarbageLetters(String email) {
        Users users = usersRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("APP ERROR"));

        return letterRepo.findAllByUserToOrUserByAndTypeOfLetter(users, TypeOfLetter.GARBAGE)
                .stream()
                .map(LetterMapper.INSTANCE::toLetterDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<LetterDto> findAllByUserWithSpamLetters(String email) {
        Users users = usersRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("APP ERROR"));

        return letterRepo.findAllByUserToOrUserByAndTypeOfLetter(users, TypeOfLetter.SPAM)
                .stream()
                .map(LetterMapper.INSTANCE::toLetterDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<LetterDto> findAllSentByTopic(String topic, Users usersBy) {
        return LetterMapper.INSTANCE.letterListToLetterDtoList(
                letterRepo.findAllByTopicContainingAndUserBy(topic, usersBy)
        );
    }

    @Override
    @Transactional
    public List<LetterDto> findAllInboxByTopic(String topic, Users usersTo) {
        return LetterMapper.INSTANCE.letterListToLetterDtoList(letterRepo.findAllByTopicContainingAndUserTo(topic, usersTo)
                .stream()
                .sorted(Comparator.comparing(Letter::getCreatedAt).reversed())
                .collect(Collectors.toList()));
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        letterRepo.findById(id).ifPresent(letter1 -> {
            letter1.getUserBy().getSendLetters().remove(letter1);
            letter1.getUserTo().getMyLetters().remove(letter1);
            letterRepo.delete(letter1);
        });
    }
}
