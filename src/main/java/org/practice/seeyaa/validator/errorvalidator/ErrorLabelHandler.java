package org.practice.seeyaa.validator.errorvalidator;

public interface ErrorLabelHandler {
    void resetLabels();
    void showEmailError(String message);
    void showPasswordError(String message);
    void showAppError();
}
