package org.practice.seeyaa.enums;

public enum FileType {
    VIDEO,
    AUDIO,
    PDF,
    WORD,
    IMAGE,
    EXECUTABLE,
    UNKNOWN;

    public FileType fromMimeType(String mimeType) {
        if (mimeType == null) return UNKNOWN;
        return switch (mimeType) {
            case String s when s.startsWith("video/") -> VIDEO;
            case String s when s.startsWith("audio/") -> AUDIO;
            case String s when s.startsWith("application/pdf") -> PDF;
            case String s when s.startsWith("application/msword") -> WORD;
            case String s when s.startsWith(
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document") -> WORD;
            default -> UNKNOWN;
        };
    }

    public static FileType fromFileExtension(String fileName) {
        if (fileName == null) return UNKNOWN;

        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return switch (extension) {
            case "mp4", "mov", "avi", "mkv" -> VIDEO;
            case "mp3", "wav", "aac" -> AUDIO;
            case "pdf" -> PDF;
            case "doc", "docx" -> WORD;
            case "jpg", "jpeg", "jpe", "gif", "png" -> IMAGE;
            case "exe", "jar" -> EXECUTABLE;
            default -> UNKNOWN;
        };
    }
}
