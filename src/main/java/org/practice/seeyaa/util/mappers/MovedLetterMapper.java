package org.practice.seeyaa.util.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.practice.seeyaa.models.dto.MovedLetterDto;
import org.practice.seeyaa.models.entity.MovedLetter;

@Mapper
public interface MovedLetterMapper {
    MovedLetterMapper INSTANCE = Mappers.getMapper(MovedLetterMapper.class);

    MovedLetterDto movedLetterToMovedLetterDto(MovedLetter movedLetter);
}
