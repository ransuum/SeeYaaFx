package org.practice.seeyaa.util;

import org.practice.seeyaa.models.dto.*;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.practice.seeyaa.models.entity.*;
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

    LetterWithAnswers toLetterWithAnswers(Letter letter);

    MessageDto toMessageDto(Message message);

    ChatDto toChatDto(Chat chat);

    UsersWithMessagesDto toUsersWithMessagesDto(Users users);
}
