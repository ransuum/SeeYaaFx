package org.practice.seeyaa.service;

import jakarta.validation.Valid;
import org.practice.seeyaa.models.dto.LetterDto;
import org.practice.seeyaa.models.dto.LetterWithAnswers;
import org.practice.seeyaa.models.dto.UsersDto;
import org.practice.seeyaa.models.entity.Letter;
import org.practice.seeyaa.models.entity.Users;
import org.practice.seeyaa.models.request.LetterRequest;

import java.util.List;

public interface LetterService {
    Letter sendLetter(@Valid LetterRequest letterRequest);

    void setLetterToSpam(String letterId, String email);

    void setLetterToGarbage(String letterId, String email);

    LetterWithAnswers findById(String id);

    List<LetterDto> findAllSentByTopic(String topic, String userTo);

    List<LetterDto> findAllInboxByTopic(String topic, String userBy);

    void deleteById(String id);
}
