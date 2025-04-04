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
import lombok.Getter;
import org.practice.seeyaa.enums.TypeOfLetter;
import org.practice.seeyaa.models.dto.LetterDto;
import org.practice.seeyaa.models.dto.LetterWithAnswers;
import org.practice.seeyaa.models.dto.UsersDto;
import org.practice.seeyaa.service.LetterService;
import org.practice.seeyaa.service.UsersService;
import org.practice.seeyaa.util.choicesofletters.Choice;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.practice.seeyaa.util.datecheck.DateChecking.checkDate;

@Component
public class EmailController {
    @FXML @Getter private Text emailOfAuthUser;
    @FXML private Button sent;
    @FXML private Button deleteButton;
    @FXML private Button inboxes;
    @FXML private Button spambutton;
    @FXML private Button spam;
    @FXML private Button garbage;
    @FXML private Button write;
    @FXML private ImageView searchButton;
    @FXML private TextField search;
    @FXML @Getter private VBox hboxInsideInboxes;
    @FXML private ImageView editProfile;

    private final ConfigurableApplicationContext springContext;
    private final LetterService letterService;
    private final UsersService usersService;
    private final Map<TypeOfLetter, Choice> typeOfLetterChoices;

    private final Map<String, Stage> openStages = new HashMap<>();

    private Stage stage;
    private Scene scene;
    private Parent root;

    public EmailController(ConfigurableApplicationContext springContext, LetterService letterService, UsersService usersService, List<Choice> choices) {
        this.springContext = springContext;
        this.letterService = letterService;
        this.usersService = usersService;
        this.typeOfLetterChoices = choices.stream().collect(Collectors.toMap(Choice::getChoice, o -> o));
    }

    @FXML
    public void initialize() {
        emailOfAuthUser.setText(SecurityContextHolder.getContext().getAuthentication().getName());
        write.setOnMouseClicked(mouseEvent -> write());
        editProfile.setOnMouseClicked(mouseEvent -> editProfile());

        addToBox(inboxes, 1, TypeOfLetter.INBOXES);
        addToBox(sent, 2, TypeOfLetter.SENT);
        addToBox(spam, 3, TypeOfLetter.SPAM);
        addToBox(garbage, 4, TypeOfLetter.GARBAGE);
        registerSearchHandlers();
    }

    @FXML
    public void delete() {
        processSelectedLetters(TypeOfLetter.GARBAGE);
    }

    @FXML
    public void spam() {
        processSelectedLetters(TypeOfLetter.SPAM);
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
                letterService.findAllInboxByTopic(search.getText(), emailOfAuthUser.getText())
                        .forEach(letter
                                -> addLetterToUI(letter, 1));
            else if (sent.getStyleClass().contains("selected"))
                letterService.findAllSentByTopic(search.getText(), emailOfAuthUser.getText())
                        .forEach(letter
                                -> addLetterToUI(letter, 2));

        });
    }

    private void write() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("send.fxml"));
            fxmlLoader.setControllerFactory(springContext::getBean);
            root = fxmlLoader.load();
            SendLetterController controller = fxmlLoader.getController();
            controller.setHiding(emailOfAuthUser.getText());
            stage = new Stage();
            scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("static/sendLetter.css")).toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Send Letter");
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void editProfile() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("edit.fxml"));
            fxmlLoader.setControllerFactory(springContext::getBean);
            this.root = fxmlLoader.load();

            UsersDto byEmail = usersService.findByEmailWithoutLists();

            EditController controller = fxmlLoader.getController();
            controller.getEmail().setText(byEmail.email());
            controller.getFirstname().setText(byEmail.firstname());
            controller.getLastname().setText(byEmail.lastname());
            controller.getUsername().setText(byEmail.username());
            stage = new Stage();
            scene = new Scene(this.root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("static/edit.css")).toExternalForm());
            stage.centerOnScreen();
            stage.widthProperty().addListener((obs, oldVal, newVal) -> this.stage.centerOnScreen());
            stage.heightProperty().addListener((obs, oldVal, newVal) -> this.stage.centerOnScreen());
            stage.setScene(this.scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addToBox(Button button, int index, TypeOfLetter choice) {
        button.setOnMouseClicked(event -> {
            resetButtonStyles();
            hboxInsideInboxes.getChildren().clear();
            button.getStyleClass().add("selected");

            List<LetterDto> letters = typeOfLetterChoices.get(choice)
                    .addToBox(index, emailOfAuthUser.getText());

            if (letters.isEmpty()) {
                Text noLetters = new Text("No letters found in this category");
                noLetters.setTextAlignment(TextAlignment.CENTER);
                hboxInsideInboxes.getChildren().add(noLetters);
            } else
                letters.stream()
                        .sorted(Comparator.comparing(LetterDto::createdAt).reversed()).toList()
                        .forEach(letter -> addLetterToUI(letter, index));
        });
    }

    public void addLetterToUI(LetterDto letter, int function) {
        TextField textField = createTextField(letter, function);
        textField.getStylesheets().add(Objects.requireNonNull(getClass().getResource("static/letters.css")).toExternalForm());
        TextField textField1 = new TextField();
        textField1.setAlignment(Pos.CENTER);
        textField1.setEditable(false);
        textField1.getStylesheets().add(Objects.requireNonNull(getClass().getResource("static/date.css")).toExternalForm());
        textField1.setText(checkDate(letter.createdAt()));

        CheckBox checkBox = new CheckBox();
        checkBox.setOnAction(event -> {
            textField.setDisable(checkBox.isSelected());
            textField1.setDisable(checkBox.isSelected());
            updateDeleteButtonVisibility();
        });

        HBox hBox = new HBox(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().addAll(checkBox, textField, textField1);

        textField.setOnMouseClicked(textFieldEvent -> {
            if (!checkBox.isSelected()) handleTextFieldClick(letter.id(), function);
        });

        hBox.setId(letter.id());
        hboxInsideInboxes.getChildren().add(hBox);
    }

    private void updateDeleteButtonVisibility() {
        final boolean anySelected = hboxInsideInboxes.getChildren().stream()
                .filter(HBox.class::isInstance)
                .map(HBox.class::cast)
                .flatMap(hbox -> hbox.getChildren().stream())
                .filter(CheckBox.class::isInstance)
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

    private void processSelectedLetters(TypeOfLetter typeOfLetter) {
        final List<HBox> selectedBoxes = new LinkedList<>();

        hboxInsideInboxes.getChildren().forEach(node -> {
            if (node instanceof HBox hBox) {
                CheckBox checkBox = (CheckBox) hBox.getChildren().getFirst();
                if (checkBox.isSelected()) selectedBoxes.add(hBox);
            }
        });

        selectedBoxes.forEach(hBox -> {
            switch (typeOfLetter) {
                case SPAM -> letterService.setLetterToSpam(hBox.getId(), emailOfAuthUser.getText());
                case GARBAGE -> letterService.setLetterToGarbage(hBox.getId(), emailOfAuthUser.getText());
                default -> letterService.deleteById(hBox.getId());
            }
        });

        hboxInsideInboxes.getChildren().removeAll(selectedBoxes);
        spambutton.setVisible(false);
        deleteButton.setVisible(false);
    }

    private TextField createTextField(LetterDto letter, int function) {
        TextField textField = new TextField();
        textField.setCursor(Cursor.HAND);
        textField.setPrefWidth(600);
        textField.setId(letter.id());

        String byName = ((function == 1) ?
                String.format("%-25s", " By: " + letter.userBy().firstname() + " " + letter.userBy().lastname())
                : String.format("%-25s", " To: " + letter.userTo().firstname() + " " + letter.userTo().lastname()));
        String paddedTopic = String.format("%-30s", letter.topic());

        textField.setText(byName + paddedTopic);
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
            controller.setCurrentEmail(emailOfAuthUser.getText());
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
