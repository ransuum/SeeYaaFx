package org.practice.seeyaa.service;

import org.practice.seeyaa.models.request.AnswerRequestDto;

@FunctionalInterface
public interface AnswerService {
    void createAnswer(AnswerRequestDto answerRequestDto, String emailBy, String idOfLetter);
}
