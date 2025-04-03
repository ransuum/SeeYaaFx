package org.practice.seeyaa.util.letterlistener;

import org.practice.seeyaa.enums.TypeOfLetter;
import org.practice.seeyaa.models.entity.MovedLetter;
import org.practice.seeyaa.repo.LetterRepo;
import org.practice.seeyaa.repo.MovedLetterRepo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class LetterDeletionScheduler {

    private final MovedLetterRepo movedLetterRepo;
    private final LetterRepo letterRepo;

    public LetterDeletionScheduler(MovedLetterRepo movedLetterRepo,
                                   LetterRepo letterRepo) {
        this.movedLetterRepo = movedLetterRepo;
        this.letterRepo = letterRepo;
    }

    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void deleteExpiredLetters() {
        LocalDateTime now = LocalDateTime.now();

        List<MovedLetter> expiredLetters = movedLetterRepo
                .findByWillDeleteAtBeforeAndTypeOfLetter(now, TypeOfLetter.GARBAGE);

        expiredLetters.forEach(movedLetter -> {
            letterRepo.delete(movedLetter.getLetter());
            movedLetterRepo.delete(movedLetter);
        });
    }
}
