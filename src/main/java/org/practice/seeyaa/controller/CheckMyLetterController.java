package org.practice.seeyaa.controller;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.kordamp.ikonli.javafx.FontIcon;
import org.practice.seeyaa.action.FileAction;
import org.practice.seeyaa.enums.FileType;
import org.practice.seeyaa.models.dto.AnswerDto;
import org.practice.seeyaa.models.dto.FileMetadataDto;
import org.practice.seeyaa.models.dto.LetterWithAnswers;
import org.practice.seeyaa.models.entity.Files;
import org.practice.seeyaa.service.StorageService;
import org.practice.seeyaa.util.triofunction.Trio;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static org.practice.seeyaa.util.fieldvalidation.FieldUtil.refractorDate;

@Component
@Slf4j
public class CheckMyLetterController {
    @FXML
    private Label email;
    @FXML
    private LetterWithAnswers letterDto;
    @FXML
    private Label firstNameLast;
    @FXML
    private VBox answers;
    @FXML
    private VBox filesContainer;
    @FXML
    private TextArea textOfLetter;
    @FXML
    private TextField topic;
    @Setter
    private String currentEmail;

    private Stage stage;

    private final ConfigurableApplicationContext springContext;
    private final StorageService storageService;
    private final AIController aiController;
    private final FileAction fileAction;

    public CheckMyLetterController(ConfigurableApplicationContext springContext, StorageService storageService,
                                   AIController aiController, FileAction fileAction) {
        this.springContext = springContext;
        this.storageService = storageService;
        this.aiController = aiController;
        this.fileAction = fileAction;
    }

    public void quit(ActionEvent event) {
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void helpToUnderstandText() {
        try {
            final var fxmlLoader = new FXMLLoader(getClass().getResource("ai-response.fxml"));
            fxmlLoader.setControllerFactory(springContext::getBean);
            Parent root = fxmlLoader.load();

            final var prompt = String.format("""
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

            final var aiStage = new Stage();
            final var scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("static/checkLetter.css")).toExternalForm());
            aiStage.setScene(scene);
            aiStage.setTitle("AI Analysis");
            aiStage.initModality(Modality.APPLICATION_MODAL);
            aiStage.show();
        } catch (IOException e) {
            log.error("Error opening AI response window", e);
            fileAction.showAlert(Alert.AlertType.ERROR, "AI Error", "Could not open AI analysis window: " + e.getMessage());
        }
    }

    @FXML
    public void answer() {
        try {
            answerOnLetter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setLetter(LetterWithAnswers letter1, int function) {
        this.letterDto = letter1;
        setLetterContent(letter1.topic(), letter1.text(),
                (function == 1) ? letter1.userBy().email()
                        : letter1.userTo().email(),
                (function == 1) ? letterDto.userBy().firstname() + " " + letterDto.userBy().lastname()
                        : letterDto.userTo().firstname() + " " + letterDto.userTo().lastname());

        loadAnswers();

        loadFileMetadataAsync();
    }

    private void setLetterContent(String topic, String text, String byEmail, String fullName) {
        this.topic.setText(topic);
        this.textOfLetter.setText(text);
        this.email.setText("Email:  " + byEmail);
        this.firstNameLast.setText(fullName);
    }

    private void loadAnswers() {
        answers.getChildren().clear();
        answers.setSpacing(10);
        answers.setPadding(new Insets(10));

        final var answersTransition = new SequentialTransition();
        final var answersList = letterDto.answers().stream()
                .sorted(Comparator.comparing(AnswerDto::createdAt).reversed())
                .toList();

        IntStream.range(0, answersList.size()).forEach(i -> {
            final var answerRow = createAnswerRow(answersList.get(i));

            answerRow.setOpacity(0);
            answerRow.setTranslateX(-20);

            final var parallelTransition = new ParallelTransition();

            final var fadeIn = new FadeTransition(Duration.millis(300), answerRow);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            final var slideIn = new TranslateTransition(Duration.millis(300), answerRow);
            slideIn.setFromX(-20);
            slideIn.setToX(0);

            parallelTransition.getChildren().addAll(fadeIn, slideIn);
            parallelTransition.setDelay(Duration.millis(i * 100d));

            answers.getChildren().add(answerRow);
            answersTransition.getChildren().add(parallelTransition);
        });

        answersTransition.play();
    }

    private void loadFileMetadataAsync() {
        final var loadingLabel = new Label("Loading file information...");
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxSize(25, 25);

        final var loadingBox = new HBox(10, progressIndicator, loadingLabel);
        loadingBox.setAlignment(Pos.CENTER);
        loadingBox.setPadding(new Insets(10));

        filesContainer.getChildren().clear();
        filesContainer.getChildren().add(loadingBox);

        Task<List<FileMetadataDto>> fileMetadataTask = new Task<>() {
            @Override
            protected List<FileMetadataDto> call() {
                return storageService.getFileMetadataByLetterId(letterDto.id());
            }
        };

        fileMetadataTask.setOnSucceeded(event -> {
            List<FileMetadataDto> fileMetadataList = fileMetadataTask.getValue();
            Platform.runLater(() -> displayFileMetadata(fileMetadataList));
        });

        fileMetadataTask.setOnFailed(event -> {
            log.error("Failed to load file metadata", fileMetadataTask.getException());
            Platform.runLater(() -> {
                filesContainer.getChildren().clear();
                Label errorLabel = new Label("Failed to load file information: " +
                        fileMetadataTask.getException().getMessage());
                errorLabel.getStyleClass().add("error-label");
                filesContainer.getChildren().add(errorLabel);
            });
        });

        new Thread(fileMetadataTask).start();
    }

    private void displayFileMetadata(List<FileMetadataDto> files) {
        filesContainer.getChildren().clear();

        filesContainer.getStyleClass().add("files-container");
        filesContainer.setSpacing(10);
        filesContainer.setPadding(new Insets(10));

        if (files.isEmpty()) {
            Label noFilesLabel = new Label("No attachments");
            noFilesLabel.getStyleClass().add("no-files-label");
            filesContainer.getChildren().add(noFilesLabel);
            return;
        }

        var sequentialTransition = new SequentialTransition();

        for (int i = 0; i < files.size(); i++) {
            FileMetadataDto fileMetadata = files.get(i);

            final var fileRow = createFileRow(fileMetadata);

            fileRow.setOpacity(0);
            fileRow.setTranslateY(20);

            final var parallelTransition = new ParallelTransition();

            final var fadeTransition = new FadeTransition(Duration.millis(200), fileRow);
            fadeTransition.setFromValue(0);
            fadeTransition.setToValue(1);

            final var translateTransition = new TranslateTransition(Duration.millis(200), fileRow);
            translateTransition.setFromY(20);
            translateTransition.setToY(0);

            parallelTransition.getChildren().addAll(fadeTransition, translateTransition);
            parallelTransition.setDelay(Duration.millis(i * 50d));

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

    private HBox createFileRow(FileMetadataDto fileMetadata) {
        final var fileRow = new HBox();
        fileRow.getStyleClass().add("file-row");
        fileRow.setSpacing(10);
        fileRow.setAlignment(Pos.CENTER_LEFT);
        fileRow.setMinHeight(40);

        final Node fileIcon = createFileTypeIcon(fileMetadata.type());

        final var fileInfo = new VBox(5);
        HBox.setHgrow(fileInfo, Priority.ALWAYS);

        final var nameLabel = new Label(fileMetadata.name());
        nameLabel.getStyleClass().add("file-name-label");
        nameLabel.setWrapText(true);

        final var sizeLabel = new Label(formatFileSize(fileMetadata.size()));
        sizeLabel.getStyleClass().add("file-size-label");

        fileInfo.getChildren().addAll(nameLabel, sizeLabel);

        final Trio<HBox, Button, ProgressIndicator> object = fileAction.createObjects();
        var downloadButton = object.second();
        var progressIndicator = object.third();
        var buttonBox = object.first();

        downloadButton.setOnAction(e -> handleDownload(fileMetadata, progressIndicator, downloadButton));

        setupRowHoverEffects(fileRow);

        fileRow.getChildren().addAll(fileIcon, fileInfo, buttonBox);
        return fileRow;
    }

    private void handleDownload(FileMetadataDto fileMetadata, ProgressIndicator progressIndicator, Button downloadButton) {
        progressIndicator.setVisible(true);
        downloadButton.setVisible(false);

        Task<Files> fileLoadTask = createFileLoadTask(String.valueOf(fileMetadata.id()));

        fileLoadTask.setOnSucceeded(event -> {
            Files completeFile = fileLoadTask.getValue();
            Task<Void> downloadTask = createDownloadTask(completeFile, progressIndicator, downloadButton);
            new Thread(downloadTask).start();
        });

        fileLoadTask.setOnFailed(event -> Platform.runLater(() -> {
            progressIndicator.setVisible(false);
            downloadButton.setVisible(true);
            fileAction.showAlert(Alert.AlertType.ERROR,
                    "Error",
                    "Failed to load file: " + fileLoadTask.getException().getMessage());
        }));

        new Thread(fileLoadTask).start();
    }

    private Task<Files> createFileLoadTask(String fileId) {
        return new Task<>() {
            @Override
            protected Files call() {
                return storageService.getFileById(Integer.valueOf(fileId));
            }
        };
    }

    private Task<Void> createDownloadTask(Files completeFile, ProgressIndicator progressIndicator, Button downloadButton) {
        return new Task<>() {
            @Override
            protected Void call() {
                final FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save File");
                fileChooser.setInitialFileName(completeFile.getName());

                Platform.runLater(() -> {
                    final File saveFile = fileChooser.showSaveDialog(stage);
                    if (saveFile != null) {
                        saveFileToSystem(completeFile, saveFile);
                    }
                    progressIndicator.setVisible(false);
                    downloadButton.setVisible(true);
                });
                return null;
            }
        };
    }

    private void saveFileToSystem(Files completeFile, File saveFile) {
        try (InputStream is = new ByteArrayInputStream(completeFile.getData());
             FileOutputStream fos = new FileOutputStream(saveFile)) {

            copyFileData(is, fos);
            Platform.runLater(() -> fileAction.showAlert(Alert.AlertType.INFORMATION,
                    "Success",
                    "File saved successfully"));
        } catch (IOException ex) {
            Platform.runLater(() -> fileAction.showAlert(Alert.AlertType.ERROR,
                    "Error",
                    "Failed to save file: " + ex.getMessage()));
        }
    }

    private void copyFileData(InputStream is, FileOutputStream fos) throws IOException {
        final byte[] buffer = new byte[1024 * 1024];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
            fos.write(buffer, 0, bytesRead);
        }
    }

    private void setupRowHoverEffects(HBox fileRow) {
        fileRow.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), fileRow);
            scale.setToX(1.02);
            scale.setToY(1.02);
            scale.play();
        });

        fileRow.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), fileRow);
            scale.setToX(1);
            scale.setToY(1);
            scale.play();
        });
    }

    private Node createFileTypeIcon(FileType type) {
        var icon = new FontIcon();

        switch (type) {
            case IMAGE -> icon.setIconLiteral("fas-file-image");
            case DOCUMENT -> icon.setIconLiteral("fas-file-word");
            case AUDIO -> icon.setIconLiteral("fas-file-audio");
            case VIDEO -> icon.setIconLiteral("fas-file-video");
            default -> icon.setIconLiteral("fas-file");
        }

        icon.getStyleClass().add("file-icon");
        return icon;
    }

    private String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        else if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        else if (size < 1024 * 1024 * 1024) return String.format("%.1f MB", size / (1024.0 * 1024));
        else return String.format("%.1f GB", size / (1024.0 * 1024 * 1024));
    }

    private HBox createAnswerRow(AnswerDto answerDto) {
        final var answerRow = new HBox();
        answerRow.getStyleClass().add("answer-row");
        answerRow.setSpacing(10);
        answerRow.setAlignment(Pos.CENTER_LEFT);

        final var avatar = createAvatar(answerDto.userByAnswered().firstname());

        final var contentBox = new VBox(5);
        contentBox.getStyleClass().add("answer-content");
        HBox.setHgrow(contentBox, Priority.ALWAYS);

        final var nameLabel = new Label(answerDto.userByAnswered().firstname());
        nameLabel.getStyleClass().add("answer-name");

        final var dateLabel = new Label(refractorDate(answerDto.createdAt()));
        dateLabel.getStyleClass().add("answer-date");

        final var headerBox = new HBox(10);
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
        final var avatar = new StackPane();
        avatar.getStyleClass().add("avatar");

        final var initials = new Label(firstName.substring(0, 1).toUpperCase());
        initials.getStyleClass().add("avatar-initials");

        avatar.getChildren().add(initials);
        return avatar;
    }

    private void answerOnLetter() throws IOException {
        final var fxmlLoader = new FXMLLoader(getClass().getResource("answer.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        Parent root = fxmlLoader.load();

        final AnswerController controller = fxmlLoader.getController();
        controller.setIdOfLetter(letterDto.id());
        controller.setEmailBy(currentEmail);

        stage = new Stage();
        final Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("static/answer.css")).toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Answer");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }
}
