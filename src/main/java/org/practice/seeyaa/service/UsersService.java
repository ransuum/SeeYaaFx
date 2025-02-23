package org.practice.seeyaa.service;

import jakarta.validation.Valid;
import org.practice.seeyaa.models.dto.UserWithLettersDto;
import org.practice.seeyaa.models.dto.UsersDto;
import org.practice.seeyaa.models.entity.Users;
import org.practice.seeyaa.models.request.EditRequest;
import org.practice.seeyaa.models.request.SignInRequest;
import org.practice.seeyaa.models.request.SignUpRequest;

public interface UsersService {
    void save(@Valid SignUpRequest signUp);
    UserWithLettersDto findByEmail(String email);
    UsersDto findByEmailWithoutLists(String email);
    Users findByEmailReal(String email);
    UsersDto findByEmailForPassword(@Valid SignInRequest signInRequest);
    Users findById(String id);
    void editProfile(@Valid EditRequest editRequest, String idOfUser);
}
