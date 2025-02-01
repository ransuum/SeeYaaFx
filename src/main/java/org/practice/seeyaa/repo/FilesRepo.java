package org.practice.seeyaa.repo;

import org.practice.seeyaa.models.entity.Files;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FilesRepo extends JpaRepository<Files, Integer> {
    Optional<Files> findByName(String fileName);
    List<Files> findAllByLetter_Id(String id);
}
