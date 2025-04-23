package org.practice.seeyaa.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.practice.seeyaa.enums.FileSize;
import org.practice.seeyaa.models.request.LetterRequestDto;
import org.practice.seeyaa.security.SecurityService;
import org.practice.seeyaa.service.LetterService;
import org.practice.seeyaa.service.impl.StorageServiceImpl;
import org.practice.seeyaa.configuration.fileconfiguration.PathMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


@Component
public class SendLetterController {
    @FXML
    private Button attachFile;
    @FXML
    private TextField hiding;
    @FXML
    private Button sendLetter;
    @FXML
    private TextArea text;
    @FXML
    private TextField toWhom;
    @FXML
    private TextField topic;
    @FXML
    private Label attachmentLabel;

    private final LetterService letterServiceImpl;
    private final StorageServiceImpl storageServiceImpl;
    private final SecurityService securityService;

    private Stage stage;

    private final List<File> selectedFiles = new ArrayList<>();

    public SendLetterController(LetterService letterServiceImpl, StorageServiceImpl storageServiceImpl, SecurityService securityService) {
        this.letterServiceImpl = letterServiceImpl;
        this.storageServiceImpl = storageServiceImpl;
        this.securityService = securityService;
    }

    @FXML
    public void initialize() {
        attachFile.setOnAction(event -> {
            selectedFiles.clear();
            attachFile();
        });
    }

    @FXML
    public void sendLetter(ActionEvent event) {
        final var scene = ((Node) event.getSource()).getScene();
        Platform.runLater(() -> {
            text.setDisable(true);
            attachFile.setDisable(true);
            sendLetter.setDisable(true);
            toWhom.setDisable(true);
            topic.setDisable(true);
            scene.setCursor(Cursor.WAIT);
        });

        Task<Void> uploadTask = new Task<>() {
            @Override
            protected Void call() {
                LetterRequestDto request = new LetterRequestDto(
                        text.getText(),
                        topic.getText(),
                        toWhom.getText(),
                        securityService.getCurrentUserEmail());
                final var savedLetter = letterServiceImpl.sendLetter(request);

                for (File file : selectedFiles) {
                    MultipartFile multipartFile = new PathMultipartFile(file);
                    storageServiceImpl.uploadFile(multipartFile, savedLetter.id());
                }
                return null;
            }
        };

        uploadTask.setOnSucceeded(e ->
                Platform.runLater(() -> {
                    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.close();
                    scene.setCursor(Cursor.DEFAULT);
                }));
        uploadTask.setOnFailed(e -> {
            Platform.runLater(() -> scene.setCursor(Cursor.DEFAULT));

            showAlert("Upload Failed", uploadTask.getException().getMessage());
        });

        new Thread(uploadTask).start();
    }

    private void attachFile() {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Files");
        final List<File> files = fileChooser.showOpenMultipleDialog(stage);

        if (files != null) {
            var scene = attachFile.getScene();
            Platform.runLater(() -> scene.setCursor(Cursor.WAIT));

            Task<Void> fileProcessingTask = new Task<>() {
                @Override
                protected Void call() {
                    selectedFiles.clear();
                    for (File file : files) {
                        if (file.length() > FileSize.ONE_GB.getSize()) {
                            showAlert("File Too Large", "File exceeds 10MB limit: " + file.getName());
                            continue;
                        }
                        selectedFiles.add(file);
                    }
                    return null;
                }
            };

            fileProcessingTask.setOnSucceeded(event -> {
                updateAttachmentLabel();
                scene.setCursor(Cursor.DEFAULT);
            });

            fileProcessingTask.setOnFailed(event -> {
                showAlert("Error", "Failed to process files.");
                scene.setCursor(Cursor.DEFAULT);
            });

            new Thread(fileProcessingTask).start();
        }
    }

    private void showAlert(String title, String content) {
        final Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void updateAttachmentLabel() {
        if (!selectedFiles.isEmpty())
            attachmentLabel.setText("Attachments: " + selectedFiles.size());
        else attachmentLabel.setText("");
    }

    public void setHiding(String hidingEmail) {
        hiding.setText(hidingEmail);
    }

}
