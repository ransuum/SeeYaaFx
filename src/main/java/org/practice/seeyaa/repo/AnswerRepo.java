package org.practice.seeyaa.repo;

import org.practice.seeyaa.models.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepo extends JpaRepository<Answer, String> {
}
