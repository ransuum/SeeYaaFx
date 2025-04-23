package org.practice.seeyaa.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.practice.seeyaa.models.dto.FileMetadataDto;
import org.practice.seeyaa.models.entity.Files;

@Mapper
public interface FileMapper {
    FileMapper INSTANCE = Mappers.getMapper(FileMapper.class);

    FileMetadataDto toFileMetadataDto(Files files);
}
