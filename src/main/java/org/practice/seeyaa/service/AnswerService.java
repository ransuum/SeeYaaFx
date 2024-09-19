package org.practice.seeyaa.service;

import lombok.RequiredArgsConstructor;
import org.practice.seeyaa.models.dto.AnswerDto;
import org.practice.seeyaa.models.entity.Answer;
import org.practice.seeyaa.models.entity.Letter;
import org.practice.seeyaa.models.entity.Users;
import org.practice.seeyaa.models.request.AnswerRequest;
import org.practice.seeyaa.repo.AnswerRepo;
import org.practice.seeyaa.repo.LetterRepo;
import org.practice.seeyaa.repo.UsersRepo;
import org.practice.seeyaa.util.Mapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class AnswerService {

    private final AnswerRepo answerRepo;
    private final LetterRepo letterRepo;
    private final UsersRepo usersRepo;

    public AnswerDto createAnswer(AnswerRequest answerRequest, String emailBy, String idOfLetter) {
        Letter letter = letterRepo.findById(idOfLetter)
                .orElseThrow(()
                        -> new RuntimeException("App Error"));

        Users users = usersRepo.findByEmail(emailBy)
                .orElseThrow(()
                        -> new RuntimeException("App Error"));

        Answer answer = Answer.builder()
                .answerText(answerRequest.getTextOfLetter())
                .currentLetter(letter)
                .userByAnswered(users)
                .createdAt(LocalDateTime.now())
                .build();

        letter.getAnswers().add(answer);
        users.getAnswers().add(answer);

        answerRepo.save(answer);
        usersRepo.save(users);

        return Mapper.INSTANCE.toAnswerDto(answerRepo.save(answer));
    }
}
