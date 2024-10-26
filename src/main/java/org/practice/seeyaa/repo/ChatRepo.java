package org.practice.seeyaa.repo;

import org.practice.seeyaa.models.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ChatRepo extends JpaRepository<Chat, String> {

    @Query("SELECT DISTINCT c FROM Chat c JOIN c.participants p WHERE p.email = :userEmail")
    List<Chat> findAllUserChatsJPQL(@Param("userEmail") String userEmail);
}
