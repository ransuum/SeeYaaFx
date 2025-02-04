package org.practice.seeyaa.controller;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.practice.seeyaa.enums.FileSize;
import org.practice.seeyaa.models.entity.Letter;
import org.practice.seeyaa.models.request.LetterRequest;
import org.practice.seeyaa.service.LetterService;
import org.practice.seeyaa.service.impl.StorageServiceImpl;
import org.practice.seeyaa.util.file_configuration.PathMultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


@Component
public class SendLetterController {
    @FXML private Button attachFile;
    @FXML private TextField hiding;
    @FXML private Button sendLetter;
    @FXML private TextArea text;
    @FXML private TextField toWhom;
    @FXML private TextField topic;
    @FXML private Label attachmentLabel;

    @Autowired
    private LetterService letterServiceImpl;
    @Autowired
    private StorageServiceImpl storageServiceImpl;

    private Stage stage;

    private final List<File> selectedFiles = new ArrayList<>();

    @FXML
    public void initialize() {
        attachFile.setOnAction(event -> {
            selectedFiles.clear();
            attachFile();
        });
    }

    @FXML
    public void sendLetter(ActionEvent event) throws IOException {
        Task<Void> uploadTask = new Task<>() {
            @Override
            protected Void call() {
                LetterRequest request = new LetterRequest(text.getText(), topic.getText(), toWhom.getText(), hiding.getText());
                Letter savedLetter = letterServiceImpl.sendLetter(request);

                for (File file : selectedFiles) {
                    MultipartFile multipartFile = new PathMultipartFile(file);
                    storageServiceImpl.uploadFile(multipartFile, savedLetter.getId());
                }
                return null;
            }
        };

        uploadTask.setOnSucceeded(e -> {
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        });
        uploadTask.setOnFailed(e -> showAlert("Upload Failed", uploadTask.getException().getMessage()));

        new Thread(uploadTask).start();
    }

    private void attachFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Files");
        List<File> files = fileChooser.showOpenMultipleDialog(stage);
        if (files != null) {
            for (File file : files) {
                if (file.length() > FileSize.FIVE_HUNDRED_MB.getSize()) {
                    showAlert("File Too Large", "File exceeds 10MB limit: " + file.getName());
                    continue;
                }
                selectedFiles.add(file);
            }
            updateAttachmentLabel();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
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
