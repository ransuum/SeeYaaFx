package org.practice.seeyaa.util.authField;

import jakarta.validation.ValidationException;
import javafx.scene.control.Label;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthorizationValidator implements Check{
    private Label incorrectInputEmail;
    private Label incorrectInputPassword;

    @Override
    public void checkFieldsRegistration() {

    }

    @Override
    public void checkFieldsLogin(ValidationException e) {
        Matcher matcher = Pattern.compile("findByEmailForPassword.signInRequest.email: ").matcher(e.getLocalizedMessage());
        Matcher matcher2 = Pattern.compile("findByEmailForPassword.signInRequest.password: ").matcher(e.getLocalizedMessage());

        if (matcher.find() && matcher2.find()) {
            incorrectInputEmail = new Label();
            incorrectInputEmail.setText("Email and password is blank or not correct for input");
            incorrectInputEmail.setVisible(true);
        } else if (!matcher.find() && matcher2.find()) {
            incorrectInputPassword = new Label();
            incorrectInputPassword.setText("One upper case, 9-36 length, special symbol, numbers");
            incorrectInputPassword.setVisible(true);
        } else if (!matcher2.find() && matcher.find()) {
            incorrectInputEmail = new Label();
            incorrectInputEmail.setText(e.getLocalizedMessage().replace("findByEmailForPassword.signInRequest.email: ", ""));
            incorrectInputEmail.setVisible(true);
        } else {
            incorrectInputPassword.setText("APP ERROR");
            incorrectInputEmail.setVisible(true);
        }
    }
}
