package org.practice.seeyaa.validator.errorvalidator;

import jakarta.validation.ValidationException;
import javafx.scene.control.Label;
import org.practice.seeyaa.enums.PatternError;

public final class AuthorizationValidator extends DefaultErrorLabelHandler implements Check {

    public AuthorizationValidator(Label incorrectInputEmail, Label incorrectInputPassword) {
        super(incorrectInputEmail, incorrectInputPassword);
    }

    @Override
    public void checkFieldsRegistration() {}

    @Override
    public void checkFieldsLogin(ValidationException e) {
        String errorMessage = e.getLocalizedMessage();
        boolean[] errors = new boolean[]{
                errorMessage.contains(PatternError.EMAIL_PATTERN.getValue()),
                errorMessage.contains(PatternError.PASSWORD_PATTERN.getValue())
        };

        resetLabels();

        if (errors[0] && errors[1])
            showEmailError("Email and password is blank or not correct for input");
        else if (errors[1])
            showPasswordError("One upper case, 9-36 length, special symbol, numbers");
        else if (errors[0])
            showEmailError(errorMessage.replace(PatternError.EMAIL_PATTERN.getValue(), ""));
        else showAppError();

    }
}
