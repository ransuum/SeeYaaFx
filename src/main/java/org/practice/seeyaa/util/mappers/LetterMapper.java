package org.practice.seeyaa.util.mappers;

import org.practice.seeyaa.models.dto.*;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.practice.seeyaa.models.entity.*;

import java.util.List;

@org.mapstruct.Mapper
public interface LetterMapper {

    LetterMapper INSTANCE = Mappers.getMapper(LetterMapper.class);

    LetterDto toLetterDto(Letter letter);
    List<LetterDto> letterListToLetterDtoList(List<Letter> letters);

    @Mapping(target = "sendLetters", source = "sendLetters")
    @Mapping(target = "myLetters", source = "myLetters")
    UserWithLettersDto toUserWithLettersDto(Users users);

    AnswerDto toAnswerDto(Answer answers);
    LetterWithAnswers toLetterWithAnswers(Letter letter);
}
