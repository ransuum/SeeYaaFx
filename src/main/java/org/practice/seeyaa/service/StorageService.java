package org.practice.seeyaa.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String uploadImage(MultipartFile file, String id);
    byte[] downloadImage(String fileName);
}
