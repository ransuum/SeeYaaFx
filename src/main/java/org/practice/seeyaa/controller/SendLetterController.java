package org.practice.seeyaa.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.practice.seeyaa.models.request.LetterRequest;
import org.practice.seeyaa.service.UsersService;
import org.practice.seeyaa.service.LetterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SendLetterController {

    @FXML
    private TextArea text;

    @FXML
    private TextField toWhom;

    @FXML
    private TextField topic;

    @FXML
    private TextField hiding;

    @Autowired
    private LetterService letterService;

    @Autowired
    private UsersService usersService;

    private Stage stage;

    public void sendLetter(ActionEvent event) throws IOException {

        LetterRequest letterRequest = LetterRequest.builder()
                .topic(topic.getText())
                .text(text.getText())
                .userTo(toWhom.getText())
                .userBy(hiding.getText())
                .build();

        letterService.sendLetter(letterRequest);

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void helpRefractorTextByAi(ActionEvent event) {

    }

    public void setHiding(String hidingEmail){
        hiding.setText(hidingEmail);
    }

}
