package org.practice.seeyaa.repo;

import org.practice.seeyaa.models.entity.Users;
import org.practice.seeyaa.models.entity.Letter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LetterRepo extends JpaRepository<Letter, String> {
    List<Letter> findAllByUserTo(Users user);
    List<Letter> findAllByUserBy(Users user);
    List<Letter> findAllByTopicContainingAndUserBy(String topic, Users userBy);
    List<Letter> findAllByTopicContainingAndUserTo(String topic, Users userTo);
}
