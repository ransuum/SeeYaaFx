package org.practice.seeyaa.service;

import jakarta.validation.Valid;
import org.practice.seeyaa.models.dto.UserWithLettersDto;
import org.practice.seeyaa.models.dto.UsersDto;
import org.practice.seeyaa.models.request.EditRequestDto;
import org.practice.seeyaa.models.request.SignUpRequestDto;

public interface UsersService {
    void save(@Valid SignUpRequestDto signUp);

    UserWithLettersDto findByEmail();

    UsersDto findByEmailWithoutLists();

    void editProfile(@Valid EditRequestDto editRequestDto);
}
