package org.practice.seeyaa.models.dto;

import lombok.NonNull;
import org.practice.seeyaa.enums.FileType;

import java.util.Arrays;
import java.util.Objects;

public record FileMetadataDto(Integer id,
                              String name,
                              FileType type,
                              Long size,
                              byte[] data) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FileMetadataDto that = (FileMetadataDto) o;
        return Objects.equals(size(), that.size())
                && Objects.equals(id(), that.id())
                && Objects.equals(name(), that.name())
                && Objects.deepEquals(data(), that.data())
                && type() == that.type();
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data());
    }

    @Override
    public @NonNull String toString() {
        return "FileMetadataDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", size=" + size +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
