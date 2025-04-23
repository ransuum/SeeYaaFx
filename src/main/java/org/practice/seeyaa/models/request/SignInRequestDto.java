package org.practice.seeyaa.models.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.practice.seeyaa.validator.email.ValidEmailTag;

public record SignInRequestDto(
        @Valid
        @Email(message = "Isn't email")
        @NotBlank(message = "Email is blank")
        @Size(max = 40, message = "Email is too long")
        @ValidEmailTag(tag = "seeyaa.com", message = "Email must end with @seeyaa.com")
        String email,

        String password) {
}
