package org.practice.seeyaa.enums;

public enum FileType {
    VIDEO,
    AUDIO,
    DOCUMENT,
    IMAGE,
    EXECUTABLE,
    UNKNOWN;

    public FileType fromMimeType(String mimeType) {
        if (mimeType == null) return UNKNOWN;

        if (mimeType.startsWith("video/")) return VIDEO;
        else if (mimeType.startsWith("audio/")) return AUDIO;
        else if (mimeType.startsWith("application/pdf") || mimeType.startsWith("application/msword")
                || mimeType.startsWith("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
            return DOCUMENT;
        else return UNKNOWN;

    }

    public static FileType fromFileExtension(String fileName) {
        if (fileName == null) return UNKNOWN;

        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        switch (extension) {
            case "mp4", "mov", "avi", "mkv" -> {
                return VIDEO;
            }
            case "mp3", "wav", "aac" -> {
                return AUDIO;
            }
            case "pdf", "doc", "docx" -> {
                return DOCUMENT;
            }
            case "jpg", "jpeg", "jpe", "gif", "png" -> {
                return IMAGE;
            }
            case "exe", "jar" -> {
                return EXECUTABLE;
            }
            default -> {
                return UNKNOWN;
            }
        }
    }
}
