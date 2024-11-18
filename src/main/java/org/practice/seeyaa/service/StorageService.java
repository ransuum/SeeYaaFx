package org.practice.seeyaa.service;

import lombok.RequiredArgsConstructor;
import org.practice.seeyaa.models.entity.Files;
import org.practice.seeyaa.repo.FilesRepo;
import org.practice.seeyaa.repo.LetterRepo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final FilesRepo filesRepo;
    private final LetterRepo letterRepo;

    public String uploadImage(MultipartFile file, String id) throws IOException {

        Files files = filesRepo.save(Files.builder()
                .name(file.getOriginalFilename())
                .letter(letterRepo.findById(id).get())
                .type(file.getContentType())
                .imageData(null).build());

        return files.getName();
    }

    public byte[] downloadImage(String fileName) {
        byte[] images = null;
        return images;
    }

}
