package org.practice.seeyaa.controller;

import jakarta.validation.ValidationException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.practice.seeyaa.models.request.SignInRequest;
import org.practice.seeyaa.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SceneController {

    @FXML
    private TextField emailInput;

    @FXML
    private PasswordField password;

    @FXML
    private Text noHaveAcc;

    @FXML
    private Label incorrectInputEmail;

    @FXML
    private Label incorrectInputPassword;

    @Autowired
    private ConfigurableApplicationContext springContext;

    @Autowired
    private UsersService usersService;

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    public void initialize() {
        noHaveAcc.setOnMouseClicked(event -> {
            try {
                signUp();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    public void go(ActionEvent event) throws IOException {
        incorrectInputEmail.setVisible(false);
        incorrectInputPassword.setVisible(false);

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("email.fxml"));
            fxmlLoader.setControllerFactory(springContext::getBean);
            root = fxmlLoader.load();

            EmailController emailController = fxmlLoader.getController();
            emailController.showEmail(usersService.findByEmailForPassword(SignInRequest.builder()
                    .email(emailInput.getText())
                    .password(password.getText())
                    .build()).email());

            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("static/email.css")).toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (ValidationException e) {

            Matcher matcher = Pattern.compile("findByEmailForPassword.signInRequest.email: ").matcher(e.getLocalizedMessage());
            Matcher matcher2 = Pattern.compile("findByEmailForPassword.signInRequest.password: ").matcher(e.getLocalizedMessage());

            if (matcher.find() && matcher2.find()) {
                incorrectInputEmail.setText("Email and password is blank or not correct for input");
                incorrectInputEmail.setVisible(true);
            } else if (!matcher.find() && matcher2.find()) {
                incorrectInputPassword.setText("One upper case, 9-36 length, special symbol, numbers");
                incorrectInputPassword.setVisible(true);
            } else if (!matcher2.find() && matcher.find()) {
                incorrectInputEmail.setText(e.getLocalizedMessage().replace("findByEmailForPassword.signInRequest.email: ", ""));
                incorrectInputEmail.setVisible(true);
            } else {
                incorrectInputPassword.setText("APP ERROR");
                incorrectInputEmail.setVisible(true);
            }

        } catch (RuntimeException e) {
            incorrectInputPassword.setText("Wrong password or email");
            incorrectInputPassword.setVisible(true);
        }
    }

    private void signUp() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("signUp.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        root = fxmlLoader.load();
        stage = new Stage();
        scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("static/signUp.css")).toExternalForm());
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Sign Up");
        stage.show();
    }
}
