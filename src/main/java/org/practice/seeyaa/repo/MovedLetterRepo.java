package org.practice.seeyaa.repo;

import org.practice.seeyaa.enums.TypeOfLetter;
import org.practice.seeyaa.models.entity.Letter;
import org.practice.seeyaa.models.entity.MovedLetter;
import org.practice.seeyaa.models.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MovedLetterRepo extends JpaRepository<MovedLetter, String> {
    List<MovedLetter> findByWillDeleteAtBeforeAndTypeOfLetter(LocalDateTime now, TypeOfLetter typeOfLetter);
    List<MovedLetter> findAllByMovedByAndTypeOfLetter(Users users, TypeOfLetter typeOfLetter);
    Optional<MovedLetter> findByLetter(Letter letter);
}
