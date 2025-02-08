package org.practice.seeyaa.models.dto;

import org.practice.seeyaa.enums.TypeOfLetter;

import java.time.LocalDateTime;

public record MovedLetterDto(String id, TypeOfLetter typeOfLetter, LetterDto letter, UsersDto movedBy, LocalDateTime willDeleteAt) { }
