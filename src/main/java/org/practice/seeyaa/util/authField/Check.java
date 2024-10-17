package org.practice.seeyaa.util.authField;

import jakarta.validation.ValidationException;

public interface Check {
    void checkFieldsRegistration();
    void checkFieldsLogin(ValidationException e);
}
