package org.practice.seeyaa.service;

import jakarta.validation.Valid;
import org.practice.seeyaa.models.dto.LetterDto;
import org.practice.seeyaa.models.dto.LetterWithAnswers;
import org.practice.seeyaa.models.entity.Letter;
import org.practice.seeyaa.models.entity.Users;
import org.practice.seeyaa.models.request.LetterRequest;

import java.util.List;

public interface LetterService {
    Letter sendLetter(@Valid LetterRequest letterRequest);
    void setLetterToSpam(String letterId);
    void setLetterToGarbage(String letterId);
    LetterWithAnswers findById(String id);
    List<LetterDto> findAllByUserWithGarbageLetters(String email);
    List<LetterDto> findAllByUserWithSpamLetters(String email);
    List<LetterDto> findAllSentByTopic(String topic, Users usersBy);
    List<LetterDto> findAllInboxByTopic(String topic, Users usersTo);
    void deleteById(String id);
}
