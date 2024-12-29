package org.practice.seeyaa.service.impl;

import lombok.RequiredArgsConstructor;
import org.practice.seeyaa.models.entity.Files;
import org.practice.seeyaa.repo.FilesRepo;
import org.practice.seeyaa.repo.LetterRepo;
import org.practice.seeyaa.service.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private final FilesRepo filesRepo;
    private final LetterRepo letterRepo;

    @Override
    public String uploadImage(MultipartFile file, String id) {

        Files files = filesRepo.save(Files.builder()
                .name(file.getOriginalFilename())
                .letter(letterRepo.findById(id).get())
                .type(file.getContentType())
                .imageData(null).build());

        return files.getName();
    }

    @Override
    public byte[] downloadImage(String fileName) {
        byte[] images = null;
        return images;
    }

}
