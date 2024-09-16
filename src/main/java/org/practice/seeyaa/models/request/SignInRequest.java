package org.practice.seeyaa.models.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.practice.seeyaa.util.validator.email.ValidEmailTag;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SignInRequest {
    @Valid
    @Email(message = "Isn't email")
    @NotBlank(message = "Email is blank")
    @Size(max = 40, message = "Email is too long")
    @ValidEmailTag(tag = "seeyaa.com", message = "Email must end with @seeyaa.com")
    private String email;

    @Valid
    @NotBlank(message = "Password is blank")
    @Size(min = 8, max = 30, message = "Password size should be from 9 to 30 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_-])[A-Za-z\\d@$!%*#?&_-]+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number and one special character")
    private String password;
}
