package org.practice.seeyaa.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.practice.seeyaa.models.TypeOfLetter;
import org.practice.seeyaa.models.dto.LetterDto;
import org.practice.seeyaa.models.dto.LetterWithAnswers;
import org.practice.seeyaa.models.entity.Users;
import org.practice.seeyaa.models.request.LetterRequest;
import org.practice.seeyaa.models.entity.Letter;
import org.practice.seeyaa.repo.LetterRepo;
import org.practice.seeyaa.repo.UsersRepo;
import org.practice.seeyaa.util.mappers.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class LetterService {

    private final LetterRepo letterRepo;
    private final UsersRepo usersRepo;

    @Transactional
    public Letter sendLetter(@Valid LetterRequest letterRequest) {
        Users usersBy = usersRepo.findByEmail(letterRequest.getUserBy())
                .orElseThrow(()
                        -> new RuntimeException("User not found"));

        Users usersTo = usersRepo.findByEmail(letterRequest.getUserTo())
                .orElseThrow(()
                        -> new RuntimeException("User not found"));

        Letter letter = Letter.builder()
                .userBy(usersBy)
                .userTo(usersTo)
                .text(letterRequest.getText())
                .topic(letterRequest.getTopic())
                .createdAt(LocalDateTime.now())
                .typeOfLetter(TypeOfLetter.LETTER)
                .build();

        letterRepo.save(letter);

        usersBy.getSendLetters().add(letter);
        usersTo.getMyLetters().add(letter);

        return letter;
    }

    public void setLetterToSpam(String letterId) {
        letterRepo.findById(letterId).ifPresent(letter -> {
            letter.setTypeOfLetter(TypeOfLetter.SPAM);
            letter.setDeleteTime(LocalDateTime.now().plusDays(30));
            letterRepo.save(letter);
        });
    }

    public void setLetterToGarbage(String letterId) {
        letterRepo.findById(letterId).ifPresent(letter -> {
            letter.setTypeOfLetter(TypeOfLetter.GARBAGE);
            letter.setDeleteTime(LocalDateTime.now().plusDays(30));
            letterRepo.save(letter);
        });
    }

    @Transactional
    public LetterWithAnswers findById(String id) {
        return Mapper.INSTANCE.toLetterWithAnswers(letterRepo.findById(id)
                .orElseThrow(()
                        -> new IllegalArgumentException("No letter found with id: " + id))
        );
    }

    @Transactional
    public List<LetterDto> findAllByUserWithGarbageLetters(String email) {
        Users users = usersRepo.findByEmail(email)
                .orElseThrow(()
                        -> new RuntimeException("APP ERROR"));

        return letterRepo.findAllByUserToOrUserByAndTypeOfLetter(users, TypeOfLetter.GARBAGE)
                .stream()
                .sorted(Comparator.comparing(Letter::getCreatedAt).reversed())
                .map(Mapper.INSTANCE::toLetterDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<LetterDto> findAllByUserWithSpamLetters(String email) {
        Users users = usersRepo.findByEmail(email)
                .orElseThrow(()
                        -> new RuntimeException("APP ERROR"));

        return letterRepo.findAllByUserToOrUserByAndTypeOfLetter(users, TypeOfLetter.SPAM)
                .stream()
                .sorted(Comparator.comparing(Letter::getCreatedAt).reversed())
                .map(Mapper.INSTANCE::toLetterDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<LetterDto> findAllSentByTopic(String topic, Users usersBy) {
        return Mapper.INSTANCE.letterListToLetterDtoList(letterRepo.findAllByTopicContainingAndUserBy(topic, usersBy)
                .stream()
                .sorted(Comparator.comparing(Letter::getCreatedAt).reversed())
                .filter(letter ->
                        letter.getTypeOfLetter().equals(TypeOfLetter.LETTER))
                .collect(Collectors.toList()));
    }

    @Transactional
    public List<LetterDto> findAllInboxByTopic(String topic, Users usersTo) {
        return Mapper.INSTANCE.letterListToLetterDtoList(letterRepo.findAllByTopicContainingAndUserTo(topic, usersTo)
                .stream()
                .sorted(Comparator.comparing(Letter::getCreatedAt).reversed())
                .filter(letter ->
                        letter.getTypeOfLetter().equals(TypeOfLetter.LETTER))
                .collect(Collectors.toList()));
    }

    @Transactional
    public void deleteById(String id) {
        letterRepo.findById(id).ifPresent(letter1 -> {

            letter1.getUserBy().getSendLetters().remove(letter1);
            letter1.getUserTo().getMyLetters().remove(letter1);

            letterRepo.delete(letter1);
        });
    }
}
