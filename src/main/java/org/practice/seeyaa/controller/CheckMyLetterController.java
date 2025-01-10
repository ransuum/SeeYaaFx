package org.practice.seeyaa.controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.practice.seeyaa.models.dto.AnswerDto;
import org.practice.seeyaa.models.dto.LetterWithAnswers;
import org.practice.seeyaa.models.entity.Files;
import org.practice.seeyaa.service.LetterService;
import org.practice.seeyaa.service.StorageService;
import org.practice.seeyaa.service.impl.LetterServiceImpl;
import org.practice.seeyaa.service.impl.StorageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static org.practice.seeyaa.util.dateCheck.DateChecking.checkDate;


@Component
public class CheckMyLetterController {
    @FXML private Label email;
    @FXML private LetterWithAnswers letterDto;
    @FXML private Label firstNameLast;
    @FXML private VBox answers;
    @FXML private VBox filesContainer;
    @FXML private TextArea textOfLetter;
    @FXML private TextField topic;

    private Stage stage;

    @Autowired
    private ConfigurableApplicationContext springContext;
    @Autowired
    private StorageService storageServiceImpl;

    public void quit(ActionEvent event) {
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void helpRefractorTextByAi(ActionEvent event) {

    }

    @FXML
    public void answer(ActionEvent event) {
        try {
            answerOnLetter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setLetter(LetterWithAnswers letter1, int function) {
        this.letterDto = letter1;
        setTopicAndTextAndToWhom(letter1.topic(), letter1.text(), letter1.userBy().email(),
                (function == 1) ? letterDto.userBy().firstname() + " " + letterDto.userBy().lastname()
                        : letterDto.userTo().firstname() + " " + letterDto.userTo().lastname());
    }

    private void setTopicAndTextAndToWhom(String topic, String text, String byEmail, String fullName) {
        this.topic.setText(topic);
        this.textOfLetter.setText(text);
        this.email.setText("Email:  " + byEmail);
        this.firstNameLast.setText(fullName);

        for (AnswerDto answerDto : letterDto.answers()) {

            TextField textField = new TextField();
            textField.setCursor(Cursor.HAND);
            textField.setEditable(false);
            textField.setText(answerDto.userByAnswered().firstname() + "   " + checkDate(answerDto.createdAt()));

            textField.setOnMouseClicked(mouseEvent -> textOfLetter.setText(answerDto.answerText()));

            answers.getChildren().add(textField);
        }

        displayFiles();

    }

    private void answerOnLetter() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("answer.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        Parent root = fxmlLoader.load();

        AnswerController controller = fxmlLoader.getController();
        controller.setIdOfLetter(letterDto.id());
        controller.setEmailBy(letterDto.userTo().email());

        stage = new Stage();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("static/answer.css")).toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Answer");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    private void displayFiles() {
        filesContainer.getChildren().clear();

        if (letterDto.files() == null || letterDto.files().isEmpty()) {
            Label noFiles = new Label("No attachments");
            noFiles.setStyle("-fx-text-fill: gray; -fx-padding: 10;");
            filesContainer.getChildren().add(noFiles);
            return;
        }

        for (Files file : letterDto.files()) {
            HBox fileRow = createFileRow(file);
            filesContainer.getChildren().add(fileRow);
        }
    }

    private HBox createFileRow(Files file) {
        HBox fileRow = new HBox(10);
        fileRow.setAlignment(Pos.CENTER_LEFT);
        fileRow.setPadding(new Insets(5));
        fileRow.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-radius: 5;");

        ImageView fileIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/inboxes.png"))));
        fileIcon.setFitHeight(20);
        fileIcon.setFitWidth(20);

        Label fileName = new Label(file.getName());
        fileName.setStyle("-fx-font-size: 14;");

        Button downloadBtn = new Button("Download");
        downloadBtn.setText("D");
        downloadBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        downloadBtn.setCursor(Cursor.HAND);
        downloadBtn.setOnAction(e -> downloadFile(file));

        if (isImageFile(file.getType())) {
            Button previewBtn = new Button("Preview");
            previewBtn.setText("O");
            previewBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
            previewBtn.setCursor(Cursor.HAND);
            previewBtn.setOnAction(e -> previewImage(file));

            fileRow.getChildren().addAll(fileIcon, fileName, previewBtn, downloadBtn);
        } else {
            fileRow.getChildren().addAll(fileIcon, fileName, downloadBtn);
        }

        return fileRow;
    }

    private boolean isImageFile(String contentType) {
        return contentType != null && contentType.toLowerCase().startsWith("image/");
    }

    private void downloadFile(Files file) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");
        fileChooser.setInitialFileName(file.getName());

        File saveFile = fileChooser.showSaveDialog(stage);
        if (saveFile != null) {
            try {
                byte[] fileData = storageServiceImpl.downloadImage(file.getName());
                java.nio.file.Files.write(saveFile.toPath(), fileData);

                showAlert(Alert.AlertType.INFORMATION, "Success",
                        "File downloaded successfully to: " + saveFile.getAbsolutePath());
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error",
                        "Failed to download file: " + e.getMessage());
            }
        }
    }

    private void previewImage(Files file) {
        try {
            byte[] imageData = storageServiceImpl.downloadImage(file.getName());
            Image image = new Image(new ByteArrayInputStream(imageData));

            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(400);
            imageView.setFitWidth(400);
            imageView.setPreserveRatio(true);

            Stage previewStage = new Stage();
            VBox previewBox = new VBox(10);
            previewBox.setAlignment(Pos.CENTER);
            previewBox.getChildren().add(imageView);

            Scene previewScene = new Scene(previewBox);
            previewStage.setScene(previewScene);
            previewStage.setTitle("Image Preview: " + file.getName());
            previewStage.initModality(Modality.APPLICATION_MODAL);
            previewStage.show();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Failed to preview image: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
