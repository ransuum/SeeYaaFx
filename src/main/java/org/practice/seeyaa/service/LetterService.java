package org.practice.seeyaa.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.practice.seeyaa.models.dto.LetterDto;
import org.practice.seeyaa.models.entity.Users;
import org.practice.seeyaa.models.request.LetterRequest;
import org.practice.seeyaa.models.entity.Letter;
import org.practice.seeyaa.repo.LetterRepo;
import org.practice.seeyaa.repo.UsersRepo;
import org.practice.seeyaa.util.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class LetterService {

    private final LetterRepo letterRepo;
    private final UsersRepo usersRepo;

    public LetterDto sendLetter(@Valid LetterRequest letterRequest) {

        Letter letter = Letter.builder()
                .userBy(usersRepo.findByEmail(letterRequest.getUserBy()).orElseThrow(()
                        -> new RuntimeException("User not found")))
                .userTo(usersRepo.findByEmail(letterRequest.getUserTo()).orElseThrow(()
                        -> new RuntimeException("User not found")))
                .text(letterRequest.getText())
                .topic(letterRequest.getTopic())
                .createdAt(LocalDateTime.now())
                .build();

        return Mapper.INSTANCE.toLetterDto(letterRepo.save(letter));
    }

    public LetterDto findById(String id) {
        return Mapper.INSTANCE.toLetterDto(letterRepo.findById(id)
                .orElseThrow(()
                        -> new IllegalArgumentException("No letter found with id: " + id))
        );
    }

    public List<LetterDto> findAllSentByTopic(String topic, Users usersBy) {
        return Mapper.INSTANCE.letterListToLetterDtoList(letterRepo.findAllByTopicContainingAndUserBy(topic, usersBy));
    }

    public List<LetterDto> findAllInboxByTopic(String topic, Users usersTo) {
        return Mapper.INSTANCE.letterListToLetterDtoList(letterRepo.findAllByTopicContainingAndUserTo(topic, usersTo));
    }

    public void deleteById(String id) {
        letterRepo.findById(id).ifPresent(letter1 -> {

            Users usersBy = letter1.getUserBy();
            usersBy.getSendLetters().remove(letter1);
            usersRepo.save(usersBy);

            Users usersByTo = letter1.getUserTo();
            usersByTo.getMyLetters().remove(letter1);
            usersRepo.save(usersByTo);

            letterRepo.delete(letter1);
        });
    }
}
