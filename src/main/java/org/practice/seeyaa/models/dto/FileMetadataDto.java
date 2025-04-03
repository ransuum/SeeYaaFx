package org.practice.seeyaa.models.dto;

import org.practice.seeyaa.enums.FileType;

public record FileMetadataDto(Integer id,
                              String name,
                              FileType type,
                              Long size,
                              byte[] data) {
}
