package org.practice.seeyaa.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

@Component
public class CheckSentLettersController {
    @FXML
    private TextField topic;

    @FXML
    private Label firstNameLast;

    @FXML
    private TextArea textOfLetter;

    @FXML
    private Label toWhom;

    private Stage stage;

    public void quit(ActionEvent event) {
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void helpRefractorTextByAi(ActionEvent event) {

    }

    public void answer(ActionEvent event) {

    }

    public void setTopicAndTextAndToWhom(String topic, String text, String toWhom, String fullName){
        this.topic.setText(topic);
        this.textOfLetter.setText(text);
        this.toWhom.setText("Email:  " + toWhom);
        this.firstNameLast.setText(fullName);
    }
}
