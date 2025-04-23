package org.practice.seeyaa.service.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.practice.seeyaa.models.dto.UserWithLettersDto;
import org.practice.seeyaa.models.dto.UsersDto;
import org.practice.seeyaa.models.request.EditRequestDto;
import org.practice.seeyaa.models.request.SignUpRequestDto;
import org.practice.seeyaa.security.SecurityService;
import org.practice.seeyaa.service.UsersService;
import org.practice.seeyaa.mappers.LetterMapper;
import org.practice.seeyaa.repo.UsersRepo;
import org.practice.seeyaa.mappers.UserMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import static org.practice.seeyaa.util.fieldvalidation.FieldUtil.isValid;

@Service
@Validated
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {
    private final UsersRepo usersRepo;
    private final PasswordEncoder passwordEncoder;
    private final SecurityService securityService;

    @Override
    public void save(@Valid SignUpRequestDto signUp) {
        final var user = UserMapper.INSTANCE.toUser(signUp);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        usersRepo.save(user);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    @Transactional
    public UserWithLettersDto findByEmail() {
        return LetterMapper.INSTANCE.toUserWithLettersDto(usersRepo.findByEmail(
                        securityService.getCurrentUserEmail())
                .orElseThrow(() -> new RuntimeException("Wrong password or email")));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    @Transactional
    public UsersDto findByEmailWithoutLists() {
        return UserMapper.INSTANCE.toUsersDto(usersRepo.findByEmail(
                        securityService.getCurrentUserEmail())
                .orElseThrow(() -> new RuntimeException("Wrong password or email")));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    @Transactional
    public void editProfile(@Valid EditRequestDto editRequestDto) {
        final var users = usersRepo.findByEmail(securityService.getCurrentUserEmail())
                .orElseThrow(() -> new RuntimeException("Wrong email of user"));

        if (isValid(editRequestDto.firstname())) users.setFirstname(editRequestDto.firstname());
        if (isValid(editRequestDto.password())) users.setPassword(passwordEncoder.encode(editRequestDto.password()));
        if (isValid(editRequestDto.lastname())) users.setLastname(editRequestDto.lastname());
        if (isValid(editRequestDto.username())) users.setUsername(editRequestDto.username());
        usersRepo.save(users);
    }
}
