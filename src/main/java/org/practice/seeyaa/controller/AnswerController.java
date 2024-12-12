package org.practice.seeyaa.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import lombok.Setter;
import org.practice.seeyaa.models.request.AnswerRequest;
import org.practice.seeyaa.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Setter
public class AnswerController {
    @FXML private Button circleButton;
    @FXML private Label idOfLetter;
    @FXML private TextArea textOfAnswer;

    private String emailBy;

    @Autowired
    private AnswerService answerService;

    @FXML
    void helpRefractorTextByAi(ActionEvent event) {

    }

    @FXML
    void cancel(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void setIdOfLetter(String idOfLetter) {
        this.idOfLetter.setText(idOfLetter);
    }

    @FXML
    void answer(ActionEvent event) {
        setInformation();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void setInformation(){
        answerService.createAnswer(AnswerRequest.builder()
                .textOfLetter(textOfAnswer.getText())
                .build(),
                emailBy, idOfLetter.getText());
    }

}
