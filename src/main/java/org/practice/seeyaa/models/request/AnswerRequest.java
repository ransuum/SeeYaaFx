package org.practice.seeyaa.models.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AnswerRequest {
    @NotBlank(message = "Text is blank")
    @Size(max = 5000, message = "Text is too long")
    private String textOfLetter;

}
