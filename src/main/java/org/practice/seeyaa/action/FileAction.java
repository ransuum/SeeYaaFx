package org.practice.seeyaa.action;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.practice.seeyaa.models.entity.Files;
import org.practice.seeyaa.util.triofunction.Trio;

public interface FileAction {
    HBox createFillRow(Files files, Stage stage);

    void downloadButton(Files files, Stage stage);

    Trio<HBox, Button, ProgressIndicator> createObjects();

    default void showAlert(Alert.AlertType type, String title, String content) {
        var alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
