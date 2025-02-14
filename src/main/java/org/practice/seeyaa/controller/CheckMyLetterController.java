package org.practice.seeyaa.controller;


import javafx.animation.*;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import org.practice.seeyaa.models.dto.LetterWithAnswers;
import org.practice.seeyaa.models.entity.Files;
import org.practice.seeyaa.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import static org.practice.seeyaa.util.dateCheck.DateChecking.checkDate;

@Component
@Slf4j
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
    private StorageService storageService;
    @Autowired
    private AIController aiController;

    public void quit(ActionEvent event) {
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void helpToUnderstandText(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ai-response.fxml"));
            fxmlLoader.setControllerFactory(springContext::getBean);
            Parent root = fxmlLoader.load();

            String prompt = String.format("""
                    Please analyze this letter and provide insights on language that wrote:

                    Topic: %s

                    Content:
                    %s

                    Please provide:
                    1. A summary of the main points
                    2. The key message or request
                    3. Suggested points to consider when responding
                    """, topic.getText(), textOfLetter.getText());

            aiController.setPrompt(prompt);

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("static/checkLetter.css")).toExternalForm());
            stage.setScene(scene);
            stage.setTitle("AI Analysis");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            log.error("Error opening AI response window", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open AI analysis window: " + e.getMessage());
        }
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
        setTopicAndTextAndToWhom(letter1.topic(), letter1.text(),
                (function == 1) ? letter1.userBy().email()
                        : letter1.userTo().email(),
                (function == 1) ? letterDto.userBy().firstname() + " " + letterDto.userBy().lastname()
                        : letterDto.userTo().firstname() + " " + letterDto.userTo().lastname());
    }

    private void setTopicAndTextAndToWhom(String topic, String text, String byEmail, String fullName) {
        this.topic.setText(topic);
        this.textOfLetter.setText(text);
        this.email.setText("Email:  " + byEmail);
        this.firstNameLast.setText(fullName);

        letterDto.answers().forEach(answerDto -> {
            TextField textField = new TextField();
            textField.setCursor(Cursor.HAND);
            textField.setEditable(false);
            textField.setText(answerDto.userByAnswered().firstname() + "   " + checkDate(answerDto.createdAt()));

            textField.setOnMouseClicked(mouseEvent -> textOfLetter.setText(answerDto.answerText()));

            answers.getChildren().add(textField);
        });

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
        List<Files> files = storageService.getFilesByLetterId(letterDto.id());
        filesContainer.getChildren().clear();

        filesContainer.getStyleClass().add("files-container");
        filesContainer.setSpacing(10);
        filesContainer.setPadding(new Insets(10));

        for (int i = 0; i < files.size(); i++) {
            HBox fileRow = createFileRow(files.get(i));

            fileRow.setOpacity(0);
            fileRow.setTranslateY(20);

            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.millis(i * 100),
                            new KeyValue(fileRow.opacityProperty(), 1, Interpolator.EASE_BOTH),
                            new KeyValue(fileRow.translateYProperty(), 0, Interpolator.EASE_OUT)
                    )
            );

            filesContainer.getChildren().add(fileRow);
            timeline.play();
        }

        if (files.size() > 2) {
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setContent(filesContainer);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefHeight(150);
            scrollPane.getStyleClass().add("scroll-pane");

            ((Pane) filesContainer.getParent()).getChildren().setAll(scrollPane);
        } else ((Pane) filesContainer.getParent()).getChildren().setAll(filesContainer);
    }

    private HBox createFileRow(Files file) {
        HBox hbox = new HBox();
        hbox.getStyleClass().add("file-row");
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setMinHeight(40);

        Label fileNameLabel = new Label(file.getName());
        fileNameLabel.getStyleClass().add("file-name-label");
        fileNameLabel.setWrapText(true);

        Button downloadButton = new Button("D");
        downloadButton.getStyleClass().add("download-button");
        downloadButton.setOnAction(e -> downloadFile(file));

        hbox.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), hbox);
            scale.setToX(1.02);
            scale.setToY(1.02);
            scale.play();
        });

        hbox.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), hbox);
            scale.setToX(1);
            scale.setToY(1);
            scale.play();
        });

        hbox.getChildren().addAll(fileNameLabel, downloadButton);
        return hbox;
    }

    private void downloadFile(Files file) {
        log.info("File name: {}", file.getName());
        var fileChooser = new FileChooser();
        fileChooser.setTitle("Save file");
        fileChooser.setInitialFileName(file.getName());
        var fileToSave = fileChooser.showSaveDialog(stage);

        if (fileToSave == null) return;

        try (InputStream inputStream = storageService.downloadFile(file.getId().toString());
             FileOutputStream outputStream = new FileOutputStream(fileToSave)) {

            byte[] buffer = new byte[1024 * 1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) outputStream.write(buffer, 0, bytesRead);

            showAlert(Alert.AlertType.CONFIRMATION, "Download Complete", "File downloaded successfully!");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Download Failed", "Error: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        var alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
