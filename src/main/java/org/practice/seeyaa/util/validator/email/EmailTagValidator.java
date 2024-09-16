package org.practice.seeyaa.util.validator.email;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmailTagValidator implements ConstraintValidator<ValidEmailTag, String> {

    @Value("${email.tag}")
    private String configuredTag;

    private String annotationTag;

    @Override
    public void initialize(ValidEmailTag constraintAnnotation) {
        this.annotationTag = constraintAnnotation.tag();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        String tagToUse = !annotationTag.isEmpty() ? annotationTag : configuredTag;
        return value.toLowerCase().endsWith("@" + tagToUse.toLowerCase());
    }
}
