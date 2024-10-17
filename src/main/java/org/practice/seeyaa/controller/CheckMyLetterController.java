package org.practice.seeyaa.controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.practice.seeyaa.models.dto.AnswerDto;
import org.practice.seeyaa.models.dto.LetterDto;
import org.practice.seeyaa.models.dto.LetterWithAnswers;
import org.practice.seeyaa.service.LetterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

import static org.practice.seeyaa.util.dateCheck.DateChecking.checkDate;


@Component
public class CheckMyLetterController {

    @FXML
    private Label email;

    private LetterWithAnswers letterDto;

    @FXML
    private Label firstNameLast;

    @FXML
    private VBox answers;

    @FXML
    private TextArea textOfLetter;

    @FXML
    private TextField topic;

    private Stage stage;
    private Parent root;
    private Scene scene;

    @Autowired
    private ConfigurableApplicationContext springContext;

    @Autowired
    private LetterService letterService;

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

    private void setTopicAndTextAndToWhom(String topic, String text, String byEmail, String fullName){
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
    }

    private void answerOnLetter() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("answer.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        root = fxmlLoader.load();

        AnswerController controller = fxmlLoader.getController();
        controller.setIdOfLetter(letterDto.id());
        controller.setEmailBy(letterDto.userTo().email());

        stage = new Stage();
        scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("static/answer.css")).toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Answer");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }


}
