package org.practice.seeyaa.service;

import org.practice.seeyaa.models.dto.MovedLetterDto;

import java.util.List;

public interface MovedLetterService {
    List<MovedLetterDto> getLettersWithSpam(String email);

    List<MovedLetterDto> getLettersWithGarbage(String email);
}
