package org.practice.seeyaa.validator.errorvalidator;

import jakarta.validation.ValidationException;

public sealed interface Check permits AuthorizationValidator {
    void checkFieldsRegistration();
    void checkFieldsLogin(ValidationException e);
}
