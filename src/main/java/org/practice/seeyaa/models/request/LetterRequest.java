package org.practice.seeyaa.models.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class LetterRequest {
    @NotBlank(message = "Text is blank")
    @Size(max = 5000, message = "Text is too long")
    private String text;

    @NotBlank(message = "Topic is blank")
    @Size(max = 62, message = "Topic is too long")
    private String topic;

    @Valid
    @Email(message = "Isn't email")
    @NotBlank(message = "Email is blank")
    @Size(max = 40, message = "Email is too long")
    @ValidEmailTag(tag = "seeyaa.com", message = "Email must end with @seeyaa.com")
    private String userTo;

    @Valid
    @Email(message = "Isn't email")
    @NotBlank(message = "Email is blank")
    @Size(max = 40, message = "Email is too long")
    @ValidEmailTag(tag = "seeyaa.com", message = "Email must end with @seeyaa.com")
    private String userBy;
}
