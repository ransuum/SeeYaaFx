package org.practice.seeyaa.repo;

import org.practice.seeyaa.models.enums.TypeOfLetter;
import org.practice.seeyaa.models.entity.Users;
import org.practice.seeyaa.models.entity.Letter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Repository
public interface LetterRepo extends JpaRepository<Letter, String> {
    List<Letter> findAllByUserTo(Users user);
    List<Letter> findAllByUserBy(Users user);
    List<Letter> findAllByTopicContainingAndUserBy(String topic, Users userBy);
    List<Letter> findAllByTopicContainingAndUserTo(String topic, Users userTo);

    @Query("SELECT l FROM Letter l WHERE (l.userTo = :user OR l.userBy = :user) AND l.typeOfLetter = :type ORDER BY l.createdAt DESC")
    List<Letter> findAllByUserToOrUserByAndTypeOfLetter(
            @Param("user") Users user,
            @Param("type") TypeOfLetter typeOfLetter
    );
}
