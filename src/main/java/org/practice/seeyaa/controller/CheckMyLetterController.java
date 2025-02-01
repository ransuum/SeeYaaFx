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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.practice.seeyaa.models.dto.AnswerDto;
import org.practice.seeyaa.models.dto.LetterWithAnswers;
import org.practice.seeyaa.models.entity.Files;
import org.practice.seeyaa.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
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
    @FXML private ScrollPane filesScrollPane;

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
        List<Files> files = storageServiceImpl.getFilesByLetterId(letterDto.id());
        filesContainer.getChildren().clear();

        filesContainer.setSpacing(10);
        filesContainer.setPadding(new Insets(10));

        files.forEach(files1 -> {
            HBox fileRow = createFileRow(files1);
            filesContainer.getChildren().add(fileRow);
        });
    }

    private HBox createFileRow(Files file) {
        HBox hbox = new HBox();
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER_LEFT);

        Label fileNameLabel = new Label(file.getName());
        fileNameLabel.setPrefWidth(300);
        fileNameLabel.setWrapText(true);

        Button downloadButton = new Button("Download");
        downloadButton.setPrefWidth(100);
        downloadButton.setOnAction(e -> downloadFile(file));

        hbox.getChildren().addAll(fileNameLabel, downloadButton);
        hbox.setPrefHeight(40);

        return hbox;
    }

    private void downloadFile(Files file) {
        try {
            byte[] data = storageServiceImpl.downloadFile(file.getId().toString());
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialFileName(file.getName());
            File fileToSave = fileChooser.showSaveDialog(stage);
            if (fileToSave != null) {
                java.nio.file.Files.write(fileToSave.toPath(), data);
                showAlert(Alert.AlertType.INFORMATION, "Success", "File downloaded successfully.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to download file: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
