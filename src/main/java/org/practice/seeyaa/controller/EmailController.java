package org.practice.seeyaa.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.practice.seeyaa.models.TypeOfLetter;
import org.practice.seeyaa.models.dto.LetterDto;
import org.practice.seeyaa.models.dto.LetterWithAnswers;
import org.practice.seeyaa.models.dto.UserWithLettersDto;
import org.practice.seeyaa.models.dto.UsersDto;
import org.practice.seeyaa.service.LetterService;
import org.practice.seeyaa.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.practice.seeyaa.util.dateCheck.DateChecking.checkDate;

@Component
public class EmailController {

    @FXML
    private Text emailOfAuthUser;

    @FXML
    private Button sent;

    @FXML
    private Button deleteButton;

    @FXML
    private Button inboxes;

    @FXML
    private TextField speakWith;

    @FXML
    private Button startChat;

    @FXML
    private Button spambutton;

    @FXML
    private Button spam;

    @FXML
    private Button garbage;

    @FXML
    private Button write;

    @FXML
    private ImageView searchButton;

    @FXML
    private TextField search;

    @FXML
    private VBox hboxInsideInboxes;
    private UsersDto usersDto;

    @FXML
    private ImageView editProfile;

    @Autowired
    private ConfigurableApplicationContext springContext;

    @Autowired
    private LetterService letterService;

    @Autowired
    private UsersService usersService;

    private final Map<String, Stage> openStages = new HashMap<>();

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    public void initialize() {
        write.setOnMouseClicked(mouseEvent -> write());
        startChat.setOnMouseClicked(mouseEvent -> startChat());

        editProfile.setOnMouseClicked(mouseEvent -> editProfile());

        addToBox(inboxes, 1, "inboxes");
        addToBox(sent, 2, "sent");
        addToBox(spam, 3, "spam");
        addToBox(garbage, 4, "garbage");
        registerSearchHandlers();
    }


    public void showEmail(UsersDto usersDto) {
        emailOfAuthUser.setText(usersDto.email());
    }

    @FXML
    public void delete() {
        deleteSelectedLetters();
    }

    @FXML
    public void spam() {
        spammedSelectedLetters();
    }

    @FXML
    public void exit(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        root = fxmlLoader.load();

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("static/login.css")).toExternalForm());
        stage.centerOnScreen();
        stage.setScene(scene);
        stage.show();
    }

    private void registerSearchHandlers() {
        searchButton.setOnMouseClicked(event -> {

            hboxInsideInboxes.getChildren().clear();

            if (inboxes.getStyleClass().contains("selected"))
                letterService.findAllInboxByTopic(search.getText(), usersService.findByEmailReal(emailOfAuthUser.getText()))
                        .forEach(letter
                                -> addLetterToUI(letter, 1));

            else if (sent.getStyleClass().contains("selected"))
                letterService.findAllSentByTopic(search.getText(), usersService.findByEmailReal(emailOfAuthUser.getText()))
                        .forEach(letter
                                -> addLetterToUI(letter, 2));

        });
    }

    private void write() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("send.fxml"));
            fxmlLoader.setControllerFactory(springContext::getBean);
            this.root = fxmlLoader.load();

            SendLetterController controller = fxmlLoader.getController();
            controller.setHiding(emailOfAuthUser.getText());

            this.stage = new Stage();
            this.scene = new Scene(root);
            this.scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("static/sendLetter.css")).toExternalForm());
            this.stage.setScene(scene);
            this.stage.setTitle("Send Letter");
            this.stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void startChat() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("chat.fxml"));
            fxmlLoader.setControllerFactory(springContext::getBean);
            this.root = fxmlLoader.load();

            ChatController controller = fxmlLoader.getController();
            UserWithLettersDto byEmail = usersService.findByEmail(emailOfAuthUser.getText());
            this.stage = new Stage();
            this.scene = new Scene(root);
            this.stage.setScene(scene);
            this.stage.setTitle("Chat");
            this.stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void editProfile() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("edit.fxml"));
            fxmlLoader.setControllerFactory(springContext::getBean);
            this.root = fxmlLoader.load();

            UsersDto byEmail = usersService.findByEmailWithoutLists(emailOfAuthUser.getText());

            EditController controller = fxmlLoader.getController();
            controller.setIdOfUser(byEmail.id());
            controller.getEmail().setText(byEmail.email());
            controller.getFirstname().setText(byEmail.firstname());
            controller.getLastname().setText(byEmail.lastname());
            controller.getUsername().setText(byEmail.username());

            this.stage = new Stage();
            this.scene = new Scene(this.root);
            this.scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("static/edit.css")).toExternalForm());
            this.stage.centerOnScreen();
            this.stage.widthProperty().addListener((obs, oldVal, newVal) -> this.stage.centerOnScreen());
            this.stage.heightProperty().addListener((obs, oldVal, newVal) -> this.stage.centerOnScreen());
            this.stage.setScene(this.scene);
            this.stage.initModality(Modality.APPLICATION_MODAL);
            this.stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addToBox(Button button, int index, String choice) {
        button.setOnMouseClicked(event -> {
            resetButtonStyles();
            hboxInsideInboxes.getChildren().clear();
            button.getStyleClass().add("selected");

            List<LetterDto> letters = new ArrayList<>();

            switch (choice) {
                case "inboxes" -> letters = this.usersService.findByEmail(emailOfAuthUser.getText()).myLetters()
                        .stream()
                        .filter(letterDto -> letterDto.typeOfLetter().equals(TypeOfLetter.LETTER))
                        .collect(Collectors.toList());

                case "sent" -> letters = this.usersService.findByEmail(emailOfAuthUser.getText()).sendLetters().stream()
                        .filter(letterDto -> letterDto.typeOfLetter().equals(TypeOfLetter.LETTER))
                        .collect(Collectors.toList());

                case "spam" -> letters = this.letterService.findAllByUserWithSpamLetters(emailOfAuthUser.getText());
                case "garbage" -> letters = this.letterService.findAllByUserWithGarbageLetters(emailOfAuthUser.getText());
            }

            if (letters.isEmpty()) {
                Text noLetters = new Text("No letters found in this category");
                noLetters.setTextAlignment(TextAlignment.CENTER);
                hboxInsideInboxes.getChildren().add(noLetters);
            } else
                letters.stream()
                        .sorted(Comparator.comparing(LetterDto::createdAt)).toList()
                        .forEach(letter -> addLetterToUI(letter, index));

        });
    }

    private void addLetterToUI(LetterDto letter, int function) {
        TextField textField = createTextField(letter, function);
        textField.getStylesheets().add(Objects.requireNonNull(getClass().getResource("static/letters.css")).toExternalForm());

        CheckBox checkBox = new CheckBox();

        checkBox.setOnAction(event -> {
            textField.setDisable(checkBox.isSelected());
            updateDeleteButtonVisibility();
        });

        HBox hBox = new HBox(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().addAll(checkBox, textField);

        textField.setOnMouseClicked(textFieldEvent -> {
            if (!checkBox.isSelected()) handleTextFieldClick(letter.id(), function);
        });

        hBox.setId(letter.id());
        hboxInsideInboxes.getChildren().add(hBox);
    }

    private void updateDeleteButtonVisibility() {
        boolean anySelected = hboxInsideInboxes.getChildren().stream()
                .filter(node -> node instanceof HBox)
                .map(node -> (HBox) node)
                .flatMap(hbox -> hbox.getChildren().stream())
                .filter(child -> child instanceof CheckBox)
                .anyMatch(child -> ((CheckBox) child).isSelected());

        spambutton.setVisible(anySelected);
        deleteButton.setVisible(anySelected);
    }

    private void resetButtonStyles() {
        inboxes.getStyleClass().remove("selected");
        sent.getStyleClass().remove("selected");
        spam.getStyleClass().remove("selected");
        garbage.getStyleClass().remove("selected");
    }

    private void deleteSelectedLetters() {
        List<HBox> toRemove = new LinkedList<>();

        hboxInsideInboxes.getChildren().forEach(node -> {

            if (node instanceof HBox hBox) {
                CheckBox checkBox = (CheckBox) hBox.getChildren().getFirst();
                if (checkBox.isSelected()) toRemove.add(hBox);
            }

        });

        for (HBox hBox : toRemove) letterService.deleteById(hBox.getId());
        hboxInsideInboxes.getChildren().removeAll(toRemove);
        spambutton.setVisible(false);
        deleteButton.setVisible(false);
    }

    private void spammedSelectedLetters() {
        List<HBox> tospam = new LinkedList<>();

        hboxInsideInboxes.getChildren().forEach(node -> {

            if (node instanceof HBox hBox) {
                CheckBox checkBox = (CheckBox) hBox.getChildren().getFirst();
                if (checkBox.isSelected()) tospam.add(hBox);
            }

        });

        for (HBox hBox : tospam) letterService.setLetterToSpam(hBox.getId());
        hboxInsideInboxes.getChildren().removeAll(tospam);
        deleteButton.setVisible(false);
        spambutton.setVisible(false);
    }

    private TextField createTextField(LetterDto letter, int function) {

        TextField textField = new TextField();
        textField.setCursor(Cursor.HAND);
        textField.setPrefWidth(800);
        textField.setId(letter.id());

        String byName = ((function == 1) ?
                String.format("%-30s", " By: " + letter.userBy().firstname() + " " + letter.userBy().lastname())
                : String.format("%-30s", " To: " + letter.userTo().firstname() + " " + letter.userTo().lastname()));
        String paddedTopic = String.format("%-115s", letter.topic());

        textField.setText(byName + paddedTopic + checkDate(letter.createdAt()));
        textField.setEditable(false);

        return textField;
    }

    private void handleTextFieldClick(String letterId, int function) {

        if (openStages.containsKey(letterId)) {
            Stage existingStage = openStages.get(letterId);
            if (existingStage.isShowing()) {
                existingStage.requestFocus();
                return;
            } else openStages.remove(letterId);

        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("checkLetter.fxml"));
            fxmlLoader.setControllerFactory(springContext::getBean);
            root = fxmlLoader.load();
            LetterWithAnswers letter1 = letterService.findById(letterId);
            stage = new Stage();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Check Letter");
            CheckMyLetterController controller = fxmlLoader.getController();
            controller.setLetter(letter1, function);
            stage.widthProperty().addListener((obs, oldVal, newVal) -> stage.centerOnScreen());
            stage.heightProperty().addListener((obs, oldVal, newVal) -> stage.centerOnScreen());
            stage.centerOnScreen();
            stage.setOnCloseRequest(event -> openStages.remove(letterId));
            stage.initModality(Modality.APPLICATION_MODAL);
            openStages.put(letterId, stage);
            stage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
