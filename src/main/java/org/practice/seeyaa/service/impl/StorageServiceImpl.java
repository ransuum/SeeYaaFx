package org.practice.seeyaa.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.practice.seeyaa.enums.FileType;
import org.practice.seeyaa.models.dto.FileMetadataDto;
import org.practice.seeyaa.models.entity.Files;
import org.practice.seeyaa.repo.FilesRepo;
import org.practice.seeyaa.repo.LetterRepo;
import org.practice.seeyaa.service.StorageService;
import org.practice.seeyaa.mappers.FileMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {
    private final FilesRepo filesRepo;
    private final LetterRepo letterRepo;

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    public void uploadFile(MultipartFile file, String letterId) {
        try {
            final var letter = letterRepo.findById(letterId)
                    .orElseThrow(() -> new RuntimeException("Letter not found with id: " + letterId));

            byte[] fileData;
            try (InputStream inputStream = file.getInputStream()) {
                fileData = inputStream.readAllBytes();
            }

            filesRepo.save(Files.builder()
                    .name(file.getOriginalFilename())
                    .type(FileType.fromFileExtension(file.getOriginalFilename()))
                    .size(file.getSize())
                    .data(fileData)
                    .letter(letter)
                    .build());
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    public InputStream downloadFile(String fileId) {
        Integer id = Integer.parseInt(fileId);
        return filesRepo.findById(id)
                .map(file -> new ByteArrayInputStream(file.getData()))
                .orElseThrow(() -> new RuntimeException("File not found with id: " + fileId));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    @Transactional
    public List<Files> getFilesByLetterId(String letterId) {
        return filesRepo.findAllByLetter_Id(letterId);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    @Transactional(readOnly = true)
    public List<FileMetadataDto> getFileMetadataByLetterId(String letterId) {
        log.info("Fetching file metadata for letter ID: {}", letterId);
        return filesRepo.findAllByLetter_Id(letterId)
                .stream()
                .map(FileMapper.INSTANCE::toFileMetadataDto)
                .toList();
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    public Files getFileById(Integer fileId) {
        log.info("Loading complete file data for file ID: {}", fileId);
        return filesRepo.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found with id: " + fileId));
    }
}
