package org.practice.seeyaa.validator.errorvalidator;

import jakarta.validation.ValidationException;

public interface Check {
    void checkFieldsRegistration();
    void checkFieldsLogin(ValidationException e);
}
