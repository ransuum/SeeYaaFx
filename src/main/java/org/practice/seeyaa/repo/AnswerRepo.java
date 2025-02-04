package org.practice.seeyaa.repo;

import org.practice.seeyaa.models.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepo extends JpaRepository<Answer, String> {
}
