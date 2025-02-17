package org.practice.seeyaa.construction;

import javafx.scene.control.Alert;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.practice.seeyaa.models.entity.Files;

public interface FileAction {
    HBox createFillRow(Files files, Stage stage);
    void downloadButton(Files files, Stage stage);

    default void showAlert(Alert.AlertType type, String title, String content) {
        var alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
