package org.practice.seeyaa.models.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public record AnswerRequest (
        @NotBlank(message = "Text is blank") @Size(max = 5000, message = "Text is too long") String textOfLetter) { }
