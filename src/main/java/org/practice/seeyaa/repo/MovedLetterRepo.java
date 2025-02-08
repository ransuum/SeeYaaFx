package org.practice.seeyaa.repo;

import org.practice.seeyaa.enums.TypeOfLetter;
import org.practice.seeyaa.models.entity.MovedLetter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MovedLetterRepo extends JpaRepository<MovedLetter, String> {
    List<MovedLetter> findByWillDeleteAtBeforeAndTypeOfLetter(LocalDateTime now, TypeOfLetter typeOfLetter);

}
