package org.practice.seeyaa.util.authfield;

import jakarta.validation.ValidationException;

public interface Check {
    void checkFieldsRegistration();
    void checkFieldsLogin(ValidationException e);
}
