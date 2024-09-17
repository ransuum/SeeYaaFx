package org.practice.seeyaa.util;

import org.practice.seeyaa.models.dto.AnswerDto;
import org.practice.seeyaa.models.dto.LetterDto;
import org.practice.seeyaa.models.dto.UserWithLettersDto;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.practice.seeyaa.models.dto.UsersDto;
import org.practice.seeyaa.models.entity.Answer;
import org.practice.seeyaa.models.entity.Letter;
import org.practice.seeyaa.models.entity.Users;
import org.practice.seeyaa.models.request.SignUpRequest;

import java.util.List;

@org.mapstruct.Mapper
public interface Mapper {

    Mapper INSTANCE = Mappers.getMapper(Mapper.class);

    UsersDto toUsersDto(Users users);

    LetterDto toLetterDto(Letter letter);

    List<LetterDto> letterListToLetterDtoList(List<Letter> letters);

    @Mapping(target = "sendLetters", source = "sendLetters")
    @Mapping(target = "myLetters", source = "myLetters")
    UserWithLettersDto toUserWithLettersDto(Users users);

    Users toUser(SignUpRequest signUp);

    AnswerDto toAnswerDto(Answer answers);

    List<AnswerDto> answerListToAnswerDtoList(List<Answer> answers);


}
