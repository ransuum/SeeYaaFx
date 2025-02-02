package org.practice.seeyaa.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.practice.seeyaa.enums.FileType;
import org.practice.seeyaa.models.entity.Files;
import org.practice.seeyaa.models.entity.Letter;
import org.practice.seeyaa.repo.FilesRepo;
import org.practice.seeyaa.repo.LetterRepo;
import org.practice.seeyaa.service.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class StorageServiceImpl implements StorageService {
    private final FilesRepo filesRepo;
    private final LetterRepo letterRepo;

    public StorageServiceImpl(FilesRepo filesRepo, LetterRepo letterRepo) {
        this.filesRepo = filesRepo;
        this.letterRepo = letterRepo;
    }

    @Override
    public void uploadFile(MultipartFile file, String letterId) {
        try {
            Letter letter = letterRepo.findById(letterId)
                    .orElseThrow(() -> new RuntimeException("Letter not found with id: " + letterId));
            filesRepo.save(Files.builder()
                    .name(file.getName())
                    .type(FileType.fromFileExtension(file.getOriginalFilename()))
                    .size(file.getSize())
                    .data(file.getBytes())
                    .letter(letter)
                    .build()
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    @Override
    public byte[] downloadFile(String fileId) {
        Integer id = Integer.parseInt(fileId);
        Files file = filesRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found with id: " + fileId));
        return file.getData();
    }

    @Override
    @Transactional
    public List<Files> getFilesByLetterId(String letterId) {
        return filesRepo.findAllByLetter_Id(letterId);
    }
}
