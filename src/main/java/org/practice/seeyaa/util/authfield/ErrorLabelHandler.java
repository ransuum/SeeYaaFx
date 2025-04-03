package org.practice.seeyaa.util.authfield;

public interface ErrorLabelHandler {
    void resetLabels();
    void showEmailError(String message);
    void showPasswordError(String message);
    void showAppError();
}
