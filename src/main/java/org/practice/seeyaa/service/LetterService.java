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
import org.practice.seeyaa.util.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class LetterService {

    private final LetterRepo letterRepo;
    private final UsersRepo usersRepo;

    @Transactional
    public LetterDto sendLetter(@Valid LetterRequest letterRequest) {
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

        return Mapper.INSTANCE.toLetterDto(letter);
    }

    @Transactional
    public LetterWithAnswers findById(String id) {
        return Mapper.INSTANCE.toLetterWithAnswers(letterRepo.findById(id)
                .orElseThrow(()
                        -> new IllegalArgumentException("No letter found with id: " + id))
        );
    }

    @Transactional
    public List<LetterDto> findAllSentByTopic(String topic, Users usersBy) {
        return Mapper.INSTANCE.letterListToLetterDtoList(letterRepo.findAllByTopicContainingAndUserBy(topic, usersBy));
    }

    @Transactional
    public List<LetterDto> findAllInboxByTopic(String topic, Users usersTo) {
        return Mapper.INSTANCE.letterListToLetterDtoList(letterRepo.findAllByTopicContainingAndUserTo(topic, usersTo));
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
