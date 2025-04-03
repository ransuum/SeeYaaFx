package org.practice.seeyaa.construction;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import org.practice.seeyaa.models.entity.Files;
import org.practice.seeyaa.service.StorageService;
import org.practice.seeyaa.util.triofunction.Trio;
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

        Label fileSizeLabel = new Label(formatFileSize(files.getSize()));
        fileSizeLabel.getStyleClass().add("file-size-label");

        VBox fileInfoBox = new VBox(5);
        fileInfoBox.getChildren().addAll(fileNameLabel, fileSizeLabel);
        HBox.setHgrow(fileInfoBox, Priority.ALWAYS);

        Trio<HBox, Button, ProgressIndicator> object = createObjects();
        var downloadButton = object.second();
        var progressIndicator = object.third();
        var buttonBox = object.first();

        downloadButton.setOnAction(e -> {
            progressIndicator.setVisible(true);
            downloadButton.setVisible(false);

            Task<Void> downloadTask = new Task<>() {
                @Override
                protected Void call() {
                    downloadFile(files, stage);
                    return null;
                }
            };

            downloadTask.setOnSucceeded(event ->
                    Platform.runLater(() -> {
                        progressIndicator.setVisible(false);
                        downloadButton.setVisible(true);
                    }));

            downloadTask.setOnFailed(event ->
                    Platform.runLater(() -> {
                        progressIndicator.setVisible(false);
                        downloadButton.setVisible(true);
                        showAlert(Alert.AlertType.ERROR, "Download Failed",
                                "Error: " + downloadTask.getException().getMessage());
                    }));

            new Thread(downloadTask).start();
        });

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

        hbox.getChildren().addAll(fileInfoBox, buttonBox);
        return hbox;
    }

    private String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        else if (size < 1024 * 1024) return String.format("%.2f KB", size / 1024.0);
        else if (size < 1024 * 1024 * 1024) return String.format("%.2f MB", size / (1024.0 * 1024));
        else return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
    }

    @Override
    public void downloadButton(Files files, Stage stage) {
        downloadFile(files, stage);
    }

    @Override
    public Trio<HBox, Button, ProgressIndicator> createObjects() {
        var downloadButton = new Button("↓");
        downloadButton.getStyleClass().add("download-button");

        var progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxSize(24, 24);
        progressIndicator.setVisible(false);

        var buttonBox = new HBox(5);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().addAll(progressIndicator, downloadButton);
        return new Trio<>(buttonBox, downloadButton, progressIndicator);
    }

    private void downloadFile(Files files, Stage stage) {
        log.info("Starting download for file: {}", files.getName());

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
