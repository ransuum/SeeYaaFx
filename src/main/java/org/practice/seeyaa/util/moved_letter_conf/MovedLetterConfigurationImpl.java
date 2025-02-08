package org.practice.seeyaa.util.moved_letter_conf;

import jakarta.persistence.EntityNotFoundException;
import org.practice.seeyaa.enums.TypeOfLetter;
import org.practice.seeyaa.models.entity.MovedLetter;
import org.practice.seeyaa.repo.LetterRepo;
import org.practice.seeyaa.repo.MovedLetterRepo;
import org.practice.seeyaa.repo.UsersRepo;
import org.springframework.stereotype.Component;

@Component
public class MovedLetterConfigurationImpl implements MovedLetterConfiguration {
    private final LetterRepo letterRepo;
    private final MovedLetterRepo movedLetterRepo;
    private final UsersRepo usersRepo;

    public MovedLetterConfigurationImpl(LetterRepo letterRepo, MovedLetterRepo movedLetterRepo, UsersRepo usersRepo) {
        this.letterRepo = letterRepo;
        this.movedLetterRepo = movedLetterRepo;
        this.usersRepo = usersRepo;
    }

    @Override
    public void setLetterType(String letterId, String email, TypeOfLetter type) {
        var letter = letterRepo.findById(letterId)
                .orElseThrow(() -> new EntityNotFoundException("Letter not found"));

        var movedLetter = movedLetterRepo.findByLetter(letter)
                .orElseGet(() -> {
                    letter.setActiveLetter(false);
                    return MovedLetter.builder()
                            .letter(letter)
                            .movedBy(usersRepo.findByEmail(email)
                                    .orElseThrow(() -> new EntityNotFoundException("User not found")))
                            .build();
                });

        movedLetter.setTypeOfLetter(type);
        movedLetterRepo.save(movedLetter);
        letterRepo.save(letter);
    }
}
