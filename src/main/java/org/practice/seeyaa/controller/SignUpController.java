package org.practice.seeyaa.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.practice.seeyaa.models.request.SignUpRequest;
import org.practice.seeyaa.service.UsersService;
import org.practice.seeyaa.service.impl.UsersServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SignUpController {
    @FXML private TextField email;
    @FXML private TextField firstName;
    @FXML private TextField lastName;
    @FXML private TextField password;
    @FXML private TextField username;

    private Stage stage;

    @Autowired
    private UsersService usersServiceImpl;

    @FXML
    public void signUp(ActionEvent event) {
        registry();

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void registry(){
        usersServiceImpl.save(SignUpRequest.builder()
                .password(password.getText())
                .email(email.getText())
                .firstname(firstName.getText())
                .lastname(lastName.getText())
                .username(username.getText())
                .build());
    }
}
