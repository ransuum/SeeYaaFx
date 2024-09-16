package org.practice.seeyaa.models.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record UsersDto (
        String id,
        String email,
        String firstname,
        String lastname,
        String username
){
}
