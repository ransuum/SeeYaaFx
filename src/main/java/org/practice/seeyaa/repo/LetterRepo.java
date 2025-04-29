package org.practice.seeyaa.repo;

import org.practice.seeyaa.models.entity.Users;
import org.practice.seeyaa.models.entity.Letter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LetterRepo extends JpaRepository<Letter, String> {
    List<Letter> findAllByTopicContainingAndUserBy(String topic, Users userBy);
    List<Letter> findAllByTopicContainingAndUserTo(String topic, Users userTo);

    @Modifying
    @Query("update Letter l set l.activeLetter = :active where l.id = :id")
    void updateActiveLetterById(@Param("id") String id, @Param("active") boolean active);
}
