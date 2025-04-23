package org.practice.seeyaa.service;

import jakarta.validation.Valid;
import org.practice.seeyaa.models.dto.LetterDto;
import org.practice.seeyaa.models.dto.LetterWithAnswers;
import org.practice.seeyaa.models.request.LetterRequestDto;

import java.util.List;

public interface LetterService {
    LetterDto sendLetter(@Valid LetterRequestDto letterRequestDto);

    void setLetterToSpam(String letterId, String email);

    void setLetterToGarbage(String letterId, String email);

    LetterWithAnswers findById(String id);

    List<LetterDto> findAllSentByTopic(String topic, String userTo);

    List<LetterDto> findAllInboxByTopic(String topic, String userBy);

    void deleteById(String id);
}
