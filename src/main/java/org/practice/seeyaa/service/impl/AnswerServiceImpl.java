package org.practice.seeyaa.service.impl;

import org.practice.seeyaa.models.dto.AnswerDto;
import org.practice.seeyaa.models.entity.Answer;
import org.practice.seeyaa.models.request.AnswerRequest;
import org.practice.seeyaa.repo.AnswerRepo;
import org.practice.seeyaa.repo.LetterRepo;
import org.practice.seeyaa.repo.UsersRepo;
import org.practice.seeyaa.service.AnswerService;
import org.practice.seeyaa.util.mappers.LetterMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AnswerServiceImpl implements AnswerService {
    private final AnswerRepo answerRepo;
    private final LetterRepo letterRepo;
    private final UsersRepo usersRepo;

    public AnswerServiceImpl(AnswerRepo answerRepo, LetterRepo letterRepo, UsersRepo usersRepo) {
        this.answerRepo = answerRepo;
        this.letterRepo = letterRepo;
        this.usersRepo = usersRepo;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    @Transactional
    public AnswerDto createAnswer(AnswerRequest answerRequest, String emailBy, String idOfLetter) {
        var letter = letterRepo.findById(idOfLetter)
                .orElseThrow(() -> new RuntimeException("App Error"));

        var users = usersRepo.findByEmail(emailBy)
                .orElseThrow(() -> new RuntimeException("App Error"));

        var answer = answerRepo.save(Answer.builder()
                .answerText(answerRequest.textOfLetter())
                .currentLetter(letter)
                .userByAnswered(users)
                .createdAt(LocalDateTime.now())
                .build());

        letter.getAnswers().add(answer);
        users.getAnswers().add(answer);

        return LetterMapper.INSTANCE.toAnswerDto(answer);
    }
}
