package org.practice.seeyaa.configuration.movedletterconf;

import lombok.extern.slf4j.Slf4j;
import org.practice.seeyaa.enums.TypeOfLetter;
import org.practice.seeyaa.exception.NotFoundException;
import org.practice.seeyaa.models.entity.MovedLetter;
import org.practice.seeyaa.repo.LetterRepo;
import org.practice.seeyaa.repo.MovedLetterRepo;
import org.practice.seeyaa.repo.UsersRepo;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
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
    @Transactional
    public void setLetterType(String letterId, String email, TypeOfLetter type) {
        final var user = usersRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        final var movedLetterEntity = movedLetterRepo.findByLetterId(letterId).map(movedLetter -> {
            movedLetterRepo.delete(movedLetter);
            letterRepo.updateActiveLetterById(letterId, true);
            return movedLetter;
        }).orElseGet(() -> {
            letterRepo.updateActiveLetterById(letterId, false);
            final var letter = letterRepo.findById(letterId)
                    .orElseThrow(() -> new NotFoundException("Letter not found"));

            final var movedLetterBuild = MovedLetter.builder()
                    .letter(letter)
                    .typeOfLetter(type)
                    .movedBy(user)
                    .build();
            return movedLetterRepo.save(movedLetterBuild);
        });
        log.info("Letter that moved: {}", movedLetterEntity.getLetter().getId());
    }
}
