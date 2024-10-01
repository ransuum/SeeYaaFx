package org.practice.seeyaa.repo;

import org.practice.seeyaa.models.TypeOfLetter;
import org.practice.seeyaa.models.entity.Users;
import org.practice.seeyaa.models.entity.Letter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LetterRepo extends JpaRepository<Letter, String> {
    List<Letter> findAllByUserTo(Users user);

    List<Letter> findAllByUserBy(Users user);

    List<Letter> findAllByTopicContainingAndUserBy(String topic, Users userBy);

    List<Letter> findAllByTopicContainingAndUserTo(String topic, Users userTo);

    List<Letter> findAllByUserToOrUserByAndTypeOfLetter(Users userTo, Users userBy, TypeOfLetter typeOfLetter);
}
