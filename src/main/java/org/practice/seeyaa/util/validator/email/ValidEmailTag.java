package org.practice.seeyaa.util.validator.email;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EmailTagValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEmailTag {
    String message() default "Invalid email tag";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String tag() default "";
}