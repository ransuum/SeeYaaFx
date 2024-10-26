package org.practice.seeyaa.models.dto;

import java.util.List;
import java.util.Set;

public record ChatDto(String id, List<MessageDto> messages) {
}
