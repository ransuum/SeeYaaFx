package org.practice.seeyaa.util.fileProperties.utils;

import org.practice.seeyaa.util.fileProperties.configurator.FileConfigurator;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public class FileUtils implements FileConfigurator {

    @Override
    public String uploadFile(String fileName, MultipartFile multipartFile) {
        return "";
    }

    @Override
    public Resource downloadFile(String fileName, String filePath) {
        return null;
    }
}
