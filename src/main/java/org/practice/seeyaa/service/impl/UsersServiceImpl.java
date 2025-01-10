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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import static org.practice.seeyaa.util.dateCheck.StringCheck.checkStringParameters;

@Service
@Validated
public class UsersServiceImpl implements UsersService {
    private final UsersRepo usersRepo;

    public UsersServiceImpl(UsersRepo usersRepo) {
        this.usersRepo = usersRepo;
    }

    public void save(@Valid SignUpRequest signUp) {
        usersRepo.save(UserMapper.INSTANCE.toUser(signUp));
    }

    @Override
    @Transactional
    public UserWithLettersDto findByEmail(String email) {
        return LetterMapper.INSTANCE.toUserWithLettersDto(usersRepo.findByEmail(email)
                .orElseThrow(()
                        -> new RuntimeException("Wrong password or email")));
    }

    @Override
    @Transactional
    public UsersDto findByEmailWithoutLists(String email) {
        return UserMapper.INSTANCE.toUsersDto(usersRepo.findByEmail(email)
                .orElseThrow(()
                        -> new RuntimeException("Wrong password or email")));
    }

    @Override
    @Transactional
    public Users findByEmailReal(String email) {
        return usersRepo.findByEmail(email).orElse(null);
    }

    @Override
    public UsersDto findByEmailForPassword(@Valid SignInRequest signInRequest) {
        Users user = usersRepo.findByEmail(signInRequest.email())
                .orElseThrow(()
                        -> new RuntimeException("Wrong password or email"));

        if (user.getPassword().equals(signInRequest.password()))
            return UserMapper.INSTANCE.toUsersDto(user);
        else throw new RuntimeException("Wrong password or email");
    }

    @Override
    public Users findById(String id) {
        return usersRepo.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public UsersDto editProfile(@Valid EditRequest editRequest, String idOfUser) {
        Users users = usersRepo.findById(idOfUser).orElseThrow(() -> new RuntimeException("Wrong id of user"));

        if (checkStringParameters(editRequest.firstname())) users.setFirstname(editRequest.firstname());
        if (checkStringParameters(editRequest.password()) && !editRequest.password().equals(users.getPassword()))
            users.setPassword(editRequest.password());
        if (checkStringParameters(editRequest.lastname())) users.setLastname(editRequest.lastname());
        if (checkStringParameters(editRequest.username())) users.setUsername(editRequest.username());

        return UserMapper.INSTANCE.toUsersDto(usersRepo.save(users));
    }
}
