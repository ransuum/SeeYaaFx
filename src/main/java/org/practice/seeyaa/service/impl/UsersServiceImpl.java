package org.practice.seeyaa.service.impl;

import jakarta.validation.Valid;
import org.practice.seeyaa.models.dto.UserWithLettersDto;
import org.practice.seeyaa.models.dto.UsersDto;
import org.practice.seeyaa.models.entity.Users;
import org.practice.seeyaa.models.request.EditRequest;
import org.practice.seeyaa.models.request.SignInRequest;
import org.practice.seeyaa.models.request.SignUpRequest;
import org.practice.seeyaa.service.UsersService;
import org.practice.seeyaa.util.mappers.LetterMapper;
import org.practice.seeyaa.repo.UsersRepo;
import org.practice.seeyaa.util.mappers.UserMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import static org.practice.seeyaa.util.datecheck.StringCheck.checkStringParameters;

@Service
@Validated
public class UsersServiceImpl implements UsersService {
    private final UsersRepo usersRepo;
    private final PasswordEncoder passwordEncoder;

    public UsersServiceImpl(UsersRepo usersRepo, PasswordEncoder passwordEncoder) {
        this.usersRepo = usersRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void save(@Valid SignUpRequest signUp) {
        var user = UserMapper.INSTANCE.toUser(signUp);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        usersRepo.save(user);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    @Transactional
    public UserWithLettersDto findByEmail() {
        return LetterMapper.INSTANCE.toUserWithLettersDto(usersRepo.findByEmail(
                SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new RuntimeException("Wrong password or email")));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    @Transactional
    public UsersDto findByEmailWithoutLists() {
        return UserMapper.INSTANCE.toUsersDto(usersRepo.findByEmail(
                SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new RuntimeException("Wrong password or email")));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    @Transactional
    public Users findByEmailReal(String email) {
        return usersRepo.findByEmail(email).orElse(null);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    public UsersDto findByEmailForPassword(@Valid SignInRequest signInRequest) {
        return UserMapper.INSTANCE.toUsersDto(usersRepo.findByEmail(signInRequest.email())
                .orElseThrow(() -> new RuntimeException("Wrong password or email")));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Users findById(String id) {
        return usersRepo.findById(id).orElse(null);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    @Transactional
    public void editProfile(@Valid EditRequest editRequest) {
        Users users = usersRepo.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new RuntimeException("Wrong email of user"));

        if (checkStringParameters(editRequest.firstname())) users.setFirstname(editRequest.firstname());
        if (checkStringParameters(editRequest.password()) && !editRequest.password().equals(users.getPassword()))
            users.setPassword(passwordEncoder.encode(editRequest.password()));
        if (checkStringParameters(editRequest.lastname())) users.setLastname(editRequest.lastname());
        if (checkStringParameters(editRequest.username())) users.setUsername(editRequest.username());
        UserMapper.INSTANCE.toUsersDto(usersRepo.save(users));
    }
}
