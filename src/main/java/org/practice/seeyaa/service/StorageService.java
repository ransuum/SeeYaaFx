package org.practice.seeyaa.service;

import org.practice.seeyaa.models.entity.Files;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface StorageService {
    void uploadFile(MultipartFile file, String id);
    InputStream downloadFile(String fileId);
    List<Files> getFilesByLetterId(String letterId);
}
