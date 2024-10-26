package org.practice.seeyaa.repo;

import org.practice.seeyaa.models.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepo extends JpaRepository<Message, String> {
}
