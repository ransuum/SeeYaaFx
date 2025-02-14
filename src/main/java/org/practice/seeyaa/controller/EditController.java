package org.practice.seeyaa.controller;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;
import lombok.Data;
import org.practice.seeyaa.models.request.EditRequest;
import org.practice.seeyaa.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;


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
    private UsersService usersService;

    @FXML
    public void initialize() {
//        setupAnimations();
        setupFieldListeners();
        setupValidation();
    }

//    private void setupAnimations() {
//        profileImage.setOnMouseEntered(e -> {
//            ScaleTransition scale = new ScaleTransition(Duration.millis(200), profileImage);
//            scale.setToX(1.05);
//            scale.setToY(1.05);
//            scale.play();
//        });
//
//        profileImage.setOnMouseExited(e -> {
//            ScaleTransition scale = new ScaleTransition(Duration.millis(200), profileImage);
//            scale.setToX(1);
//            scale.setToY(1);
//            scale.play();
//        });
//    }

    private void setupFieldListeners() {
        List<Pair<CheckBox, TextField>> fieldPairs = Arrays.asList(
                new Pair<>(firstnameCheck, firstname),
                new Pair<>(lastnameCheck, lastname),
                new Pair<>(usernameCheck, username)
        );

        fieldPairs.forEach(pair -> {
            pair.getKey().setOnAction(e -> {
                boolean selected = pair.getKey().isSelected();
                FadeTransition fade = new FadeTransition(Duration.millis(300), pair.getValue());
                fade.setFromValue(selected ? 0.6 : 1);
                fade.setToValue(selected ? 1 : 0.6);
                fade.play();
                pair.getValue().setDisable(!selected);
            });
        });

        password1Check.setOnAction(e -> {
            boolean selected = password1Check.isSelected();
            ParallelTransition parallel = new ParallelTransition();

            Arrays.asList(password1, password2).forEach(field -> {
                FadeTransition fade = new FadeTransition(Duration.millis(300), field);
                fade.setFromValue(selected ? 0.6 : 1);
                fade.setToValue(selected ? 1 : 0.6);
                parallel.getChildren().add(fade);
                field.setDisable(!selected);
            });

            parallel.play();
        });
    }

    private void setupValidation() {
        password2.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!password1.getText().equals(newValue))
                password2.setStyle("-fx-background-color: rgba(255, 200, 200, 0.8);");
            else password2.setStyle(null);

        });
    }

    @FXML
    void confirm(ActionEvent event) {
        if (validateForm()) {
            Button source = (Button) event.getSource();
            RotateTransition rotate = new RotateTransition(Duration.millis(180), source);
            rotate.setByAngle(360);

            ScaleTransition scale = new ScaleTransition(Duration.millis(180), source);
            scale.setToX(0);
            scale.setToY(0);

            ParallelTransition parallel = new ParallelTransition(rotate, scale);
            parallel.setOnFinished(e -> {
                updateUser();
                Stage stage = (Stage) source.getScene().getWindow();
                stage.close();
            });

            parallel.play();
        }
    }

    private boolean validateForm() {
        if (password1Check.isSelected() && !password1.getText().equals(password2.getText())) {
            showError("Passwords do not match");
            return false;
        }
        return true;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    private void updateUser() {
        try {
            String newFirstname = firstnameCheck.isSelected() ? firstname.getText() : null;
            String newLastname = lastnameCheck.isSelected() ? lastname.getText() : null;
            String newUsername = usernameCheck.isSelected() ? username.getText() : null;
            String newPassword = password1Check.isSelected() ? password1.getText().trim() : null;

            usersService.editProfile(new EditRequest(
                    newFirstname,
                    newLastname,
                    newUsername,
                    newPassword
            ), idOfUser);
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }
}
