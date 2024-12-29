package org.practice.seeyaa.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Data;
import org.practice.seeyaa.models.request.EditRequest;
import org.practice.seeyaa.service.UsersService;
import org.practice.seeyaa.service.impl.UsersServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@Data
public class EditController {
    @FXML private TextField email;
    @FXML private TextField firstname;
    @FXML private CheckBox firstnameCheck;
    @FXML private TextField lastname;
    @FXML private CheckBox lastnameCheck;
    @FXML private TextField password1;
    @FXML private CheckBox password1Check;
    @FXML private TextField password2;
    @FXML private TextField username;
    @FXML private CheckBox usernameCheck;

    private String idOfUser;

    @Autowired
    private UsersService usersServiceImpl;

    @FXML
    public void initialize(){
        firstnameCheck.setOnAction(actionEvent -> firstname.setDisable(!firstnameCheck.isSelected()));
        lastnameCheck.setOnAction(actionEvent -> lastname.setDisable(!lastnameCheck.isSelected()));

        password1Check.setOnAction(actionEvent ->{
            password1.setDisable(!password1Check.isSelected());
            password2.setDisable(!password1Check.isSelected());
        });

        usernameCheck.setOnAction(actionEvent -> username.setDisable(!usernameCheck.isSelected()));
    }

    @FXML
    void confirm(ActionEvent event) {
        updateUser();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void updateUser(){
        if (!password1.getText().equals(password2.getText()))
            throw new RuntimeException("Passwords do not match");
        usersServiceImpl.editProfile(
                EditRequest.builder()
                        .firstname(firstname.getText())
                        .lastname(lastname.getText())
                        .username(username.getText())
                        .build(),
                idOfUser
        );

    }
}
