package org.practice.seeyaa.util.fileProperties.configurator;

import org.practice.seeyaa.enums.FileType;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileConfigurator {
    String uploadFile(String fileName, MultipartFile multipartFile);
    Resource downloadFile(String fileName, String filePath);
}
