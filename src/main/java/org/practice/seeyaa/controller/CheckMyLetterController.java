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
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import org.practice.seeyaa.models.dto.AnswerDto;
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
import java.util.stream.IntStream;

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

        answers.getChildren().clear();
        answers.setSpacing(10);
        answers.setPadding(new Insets(10));

        SequentialTransition answersTransition = new SequentialTransition();
        List<AnswerDto> answersList = letterDto.answers();

        IntStream.range(0, answersList.size()).forEach(i -> {
            HBox answerRow = createAnswerRow(answersList.get(i));

            answerRow.setOpacity(0);
            answerRow.setTranslateX(-20);

            ParallelTransition parallelTransition = new ParallelTransition();

            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), answerRow);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), answerRow);
            slideIn.setFromX(-20);
            slideIn.setToX(0);

            parallelTransition.getChildren().addAll(fadeIn, slideIn);
            parallelTransition.setDelay(Duration.millis(i * 100));

            answers.getChildren().add(answerRow);
            answersTransition.getChildren().add(parallelTransition);
        });

        answersTransition.play();
        displayFiles();
    }

    private HBox createAnswerRow(AnswerDto answerDto) {
        HBox answerRow = new HBox();
        answerRow.getStyleClass().add("answer-row");
        answerRow.setSpacing(10);
        answerRow.setAlignment(Pos.CENTER_LEFT);

        StackPane avatar = createAvatar(answerDto.userByAnswered().firstname());

        VBox contentBox = new VBox(5);
        contentBox.getStyleClass().add("answer-content");
        HBox.setHgrow(contentBox, Priority.ALWAYS);

        Label nameLabel = new Label(answerDto.userByAnswered().firstname());
        nameLabel.getStyleClass().add("answer-name");

        Label dateLabel = new Label(checkDate(answerDto.createdAt()));
        dateLabel.getStyleClass().add("answer-date");

        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.getChildren().addAll(nameLabel, dateLabel);

        contentBox.getChildren().addAll(headerBox);

        answerRow.getChildren().addAll(avatar, contentBox);

        answerRow.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), answerRow);
            scale.setToX(1.01);
            scale.setToY(1.01);
            scale.play();
        });

        answerRow.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), answerRow);
            scale.setToX(1);
            scale.setToY(1);
            scale.play();
        });

        answerRow.setOnMouseClicked(event -> {
            textOfLetter.setText(answerDto.answerText());

            FadeTransition textFade = new FadeTransition(Duration.millis(300), textOfLetter);
            textFade.setFromValue(0.5);
            textFade.setToValue(1);
            textFade.play();
        });

        return answerRow;
    }

    private StackPane createAvatar(String firstName) {
        StackPane avatar = new StackPane();
        avatar.getStyleClass().add("avatar");

        Label initials = new Label(firstName.substring(0, 1).toUpperCase());
        initials.getStyleClass().add("avatar-initials");

        avatar.getChildren().add(initials);
        return avatar;
    }

    private void answerOnLetter() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("answer.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        Parent root = fxmlLoader.load();

        AnswerController controller = fxmlLoader.getController();
        controller.setIdOfLetter(letterDto.id());
        controller.setEmailBy(letterDto.userBy().email());

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

        SequentialTransition sequentialTransition = new SequentialTransition();

        for (int i = 0; i < files.size(); i++) {
            HBox fileRow = createFileRow(files.get(i));

            fileRow.setOpacity(0);
            fileRow.setTranslateY(20);

            ParallelTransition parallelTransition = new ParallelTransition();

            FadeTransition fadeTransition = new FadeTransition(Duration.millis(200), fileRow);
            fadeTransition.setFromValue(0);
            fadeTransition.setToValue(1);

            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(200), fileRow);
            translateTransition.setFromY(20);
            translateTransition.setToY(0);

            parallelTransition.getChildren().addAll(fadeTransition, translateTransition);

            parallelTransition.setDelay(Duration.millis(i * 50));

            filesContainer.getChildren().add(fileRow);
            sequentialTransition.getChildren().add(parallelTransition);
        }

        sequentialTransition.play();

        if (files.size() > 2) {
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setContent(filesContainer);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefHeight(150);
            scrollPane.getStyleClass().add("scroll-pane");

            ((Pane) filesContainer.getParent()).getChildren().setAll(scrollPane);
        } else {
            ((Pane) filesContainer.getParent()).getChildren().setAll(filesContainer);
        }
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
        HBox.setHgrow(fileNameLabel, Priority.ALWAYS);

        Button downloadButton = new Button("↓");
        downloadButton.getStyleClass().add("download-button");
        downloadButton.setOnAction(e -> downloadFile(file));

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
