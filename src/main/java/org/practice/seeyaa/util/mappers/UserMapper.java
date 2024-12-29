package org.practice.seeyaa.util.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.practice.seeyaa.models.dto.UsersDto;
import org.practice.seeyaa.models.entity.Users;
import org.practice.seeyaa.models.request.SignUpRequest;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UsersDto toUsersDto(Users users);
    Users toUser(SignUpRequest signUp);
}
