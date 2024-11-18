package org.practice.seeyaa.util.fileProperties.configurator;

import org.practice.seeyaa.enums.FileType;

public interface FileConfigurator {
    byte[] compressFile(byte[] data);
    byte[] decompressFile(byte[] data);
    FileType getFileType();
}
