package org.practice.seeyaa.service;

import org.practice.seeyaa.models.request.AnswerRequest;

@FunctionalInterface
public interface AnswerService {
    void createAnswer(AnswerRequest answerRequest, String emailBy, String idOfLetter);
}
