package org.practice.seeyaa.controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.practice.seeyaa.service.LetterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class CheckMyLetterController {

    @FXML
    private Label byEmail;

    @FXML
    private Label firstNameLast;

    @FXML
    private TextArea textOfLetter;

    @FXML
    private TextField topic;

    private Stage stage;

    @Autowired
    private LetterService letterService;

    public void quit(ActionEvent event) {
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void helpRefractorTextByAi(ActionEvent event) {

    }

    public void answer(ActionEvent event) {

    }

    public void setTopicAndTextAndToWhom(String topic, String text, String byEmail, String fullName){
        this.topic.setText(topic);
        this.textOfLetter.setText(text);
        this.byEmail.setText("Email:  " + byEmail);
        this.firstNameLast.setText(fullName);
    }
}
