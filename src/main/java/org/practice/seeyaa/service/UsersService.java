package org.practice.seeyaa.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.practice.seeyaa.models.dto.UserWithLettersDto;
import org.practice.seeyaa.models.dto.UsersDto;
import org.practice.seeyaa.models.entity.Users;
import org.practice.seeyaa.models.request.SignInRequest;
import org.practice.seeyaa.models.request.SignUpRequest;
import org.practice.seeyaa.util.Mapper;
import org.practice.seeyaa.repo.UsersRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Validated
public class UsersService {

    private final UsersRepo usersRepo;

    public void save(@Valid SignUpRequest signUp) {
        usersRepo.save(Mapper.INSTANCE.toUser(signUp));
    }

    @Transactional
    public UserWithLettersDto findByEmail(String email) {
        return Mapper.INSTANCE.toUserWithLettersDto(usersRepo.findByEmail(email)
                .orElseThrow(()
                        -> new RuntimeException("Wrong password or email")));
    }

    @Transactional
    public Users findByEmailReal(String email) {
        return usersRepo.findByEmail(email).orElse(null);
    }

    public UsersDto findByEmailForPassword(@Valid SignInRequest signInRequest) {

        Users user = usersRepo.findByEmail(signInRequest.getEmail())
                .orElseThrow(()
                        -> new RuntimeException("Wrong password or email"));

        if (user.getPassword().equals(signInRequest.getPassword())) return Mapper.INSTANCE.toUsersDto(user);
        else throw new RuntimeException("Wrong password or email");
    }
}
