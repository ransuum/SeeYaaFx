package org.practice.seeyaa.service.impl;

import org.practice.seeyaa.enums.TypeOfLetter;
import org.practice.seeyaa.exception.NotFoundException;
import org.practice.seeyaa.models.dto.MovedLetterDto;
import org.practice.seeyaa.repo.MovedLetterRepo;
import org.practice.seeyaa.repo.UsersRepo;
import org.practice.seeyaa.service.MovedLetterService;
import org.practice.seeyaa.mappers.MovedLetterMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MovedLetterServiceImpl implements MovedLetterService {
    private final MovedLetterRepo movedLetterRepo;
    private final UsersRepo usersRepo;

    public MovedLetterServiceImpl(MovedLetterRepo movedLetterRepo, UsersRepo usersRepo) {
        this.movedLetterRepo = movedLetterRepo;
        this.usersRepo = usersRepo;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    @Transactional(readOnly = true)
    public List<MovedLetterDto> getLettersWithSpam(String email) {
        return usersRepo.findByEmail(email)
                .map(movedBy -> movedLetterRepo.findAllByMovedByAndTypeOfLetter(movedBy, TypeOfLetter.SPAM)
                        .stream()
                        .map(MovedLetterMapper.INSTANCE::movedLetterToMovedLetterDto)
                        .toList())
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    @Transactional(readOnly = true)
    public List<MovedLetterDto> getLettersWithGarbage(String email) {
        return usersRepo.findByEmail(email)
                .map(movedBy -> movedLetterRepo.findAllByMovedByAndTypeOfLetter(movedBy, TypeOfLetter.GARBAGE)
                        .stream()
                        .map(MovedLetterMapper.INSTANCE::movedLetterToMovedLetterDto)
                        .toList())
                .orElseThrow(() -> new NotFoundException("User not found"));

    }
}
