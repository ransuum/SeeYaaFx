package org.practice.seeyaa.util.authField;

public interface ErrorLabelHandler {
    void resetLabels();
    void showEmailError(String message);
    void showPasswordError(String message);
    void showAppError();
}
