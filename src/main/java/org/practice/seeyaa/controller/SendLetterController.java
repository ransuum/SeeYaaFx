package org.practice.seeyaa.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.practice.seeyaa.models.entity.Files;
import org.practice.seeyaa.models.entity.Letter;
import org.practice.seeyaa.models.request.LetterRequest;
import org.practice.seeyaa.service.StorageService;
import org.practice.seeyaa.service.UsersService;
import org.practice.seeyaa.service.LetterService;
import org.practice.seeyaa.util.imageProperties.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private LetterService letterService;

    @Autowired
    private StorageService storageService;

    private Stage stage;

    @FXML
    private Label attachmentLabel;

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

        try {
            LetterRequest letterRequest = LetterRequest.builder()
                    .topic(topic.getText())
                    .text(text.getText())
                    .userTo(toWhom.getText())
                    .userBy(hiding.getText())
                    .build();

            Letter savedLetter = letterService.sendLetter(letterRequest);

            for (File file : selectedFiles) {
                try {
                    MultipartFile multipartFile = convertToMultipartFile(file);

                    storageService.uploadImage(multipartFile, savedLetter.getId());

                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert("Error", "Failed to process file: " + file.getName());
                }
            }

            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to send letter: " + e.getMessage());
        }
    }

    private void attachFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Files to Attach");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"),
                new FileChooser.ExtensionFilter("Documents", "*.pdf", "*.doc", "*.docx", "*.txt")
        );

        List<File> files = fileChooser.showOpenMultipleDialog(attachFile.getScene().getWindow());

        if (files != null) {
            selectedFiles.addAll(files);
            updateAttachmentLabel();
        }
    }

    private void updateAttachmentLabel() {
        if (attachmentLabel != null) {
            attachmentLabel.setText("Files attached: " + selectedFiles.stream()
                    .map(File::getName)
                    .collect(Collectors.joining(", ")));
        }
    }

    private MultipartFile convertToMultipartFile(final File file) throws IOException {
        return new MultipartFile() {
            private final String name = file.getName();
            private final String originalFilename = file.getName();
            private final String contentType = java.nio.file.Files.probeContentType(file.toPath());
            private final boolean empty = file.length() == 0;
            private final long size = file.length();
            private byte[] bytes;

            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getOriginalFilename() {
                return originalFilename;
            }

            @Override
            public String getContentType() {
                return contentType;
            }

            @Override
            public boolean isEmpty() {
                return empty;
            }

            @Override
            public long getSize() {
                return size;
            }

            @Override
            public byte[] getBytes() throws IOException {
                if (bytes == null) {
                    bytes = java.nio.file.Files.readAllBytes(file.toPath());
                }
                return bytes;
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return new FileInputStream(file);
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {
                java.nio.file.Files.copy(file.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        };
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setHiding(String hidingEmail){
        hiding.setText(hidingEmail);
    }

}
