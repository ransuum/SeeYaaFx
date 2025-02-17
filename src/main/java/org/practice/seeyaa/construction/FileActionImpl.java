package org.practice.seeyaa.construction;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import org.practice.seeyaa.models.entity.Files;
import org.practice.seeyaa.service.StorageService;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
public class FileActionImpl implements FileAction {
    private final StorageService storageService;

    public FileActionImpl(StorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public HBox createFillRow(Files files, Stage stage) {
        HBox hbox = new HBox();
        hbox.getStyleClass().add("file-row");
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setMinHeight(40);

        Label fileNameLabel = new Label(files.getName());
        fileNameLabel.getStyleClass().add("file-name-label");
        fileNameLabel.setWrapText(true);
        HBox.setHgrow(fileNameLabel, Priority.ALWAYS);

        Button downloadButton = new Button("↓");
        downloadButton.getStyleClass().add("download-button");
        downloadButton.setOnAction(e -> downloadButton(files, stage));

        hbox.setOnMouseEntered(e -> {
            ParallelTransition pt = new ParallelTransition();

            ScaleTransition scale = new ScaleTransition(Duration.millis(200), hbox);
            scale.setToX(1.02);
            scale.setToY(1.02);

            FadeTransition fade = new FadeTransition(Duration.millis(200), downloadButton);
            fade.setToValue(0.8);

            pt.getChildren().addAll(scale, fade);
            pt.play();
        });

        hbox.setOnMouseExited(e -> {
            ParallelTransition pt = new ParallelTransition();

            ScaleTransition scale = new ScaleTransition(Duration.millis(200), hbox);
            scale.setToX(1);
            scale.setToY(1);

            FadeTransition fade = new FadeTransition(Duration.millis(200), downloadButton);
            fade.setToValue(1);

            pt.getChildren().addAll(scale, fade);
            pt.play();
        });

        hbox.getChildren().addAll(fileNameLabel, downloadButton);
        return hbox;
    }

    @Override
    public void downloadButton(Files files, Stage stage) {
        log.info("File name: {}", files.getName());
        var fileChooser = new FileChooser();
        fileChooser.setTitle("Save file");
        fileChooser.setInitialFileName(files.getName());
        var fileToSave = fileChooser.showSaveDialog(stage);
        if (fileToSave == null) return;

        try (InputStream inputStream = storageService.downloadFile(files.getId().toString());
             FileOutputStream outputStream = new FileOutputStream(fileToSave)) {

            byte[] buffer = new byte[1024 * 1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) outputStream.write(buffer, 0, bytesRead);

            showAlert(Alert.AlertType.CONFIRMATION, "Download Complete", "File downloaded successfully!");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Download Failed", "Error: " + e.getMessage());
        }
    }
}
