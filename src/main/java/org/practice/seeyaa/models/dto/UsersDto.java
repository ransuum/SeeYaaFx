package org.practice.seeyaa.models.dto;

import lombok.Builder;

@Builder
public record UsersDto (
        String id,
        String email,
        String firstname,
        String lastname,
        String username
){
}
