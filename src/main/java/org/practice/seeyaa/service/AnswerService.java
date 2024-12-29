package org.practice.seeyaa.service;

import org.practice.seeyaa.models.dto.AnswerDto;
import org.practice.seeyaa.models.request.AnswerRequest;

public interface AnswerService {
    AnswerDto createAnswer(AnswerRequest answerRequest, String emailBy, String idOfLetter);
}
