package org.practice.seeyaa.util.fileProperties.utils;

import org.practice.seeyaa.enums.FileType;
import org.practice.seeyaa.util.fileProperties.configurator.FileConfigurator;

public class AudioUtils implements FileConfigurator {
    @Override
    public byte[] compressFile(byte[] data) {
        return new byte[0];
    }

    @Override
    public byte[] decompressFile(byte[] data) {
        return new byte[0];
    }

    @Override
    public FileType getFileType() {
        return FileType.Audio;
    }
}
