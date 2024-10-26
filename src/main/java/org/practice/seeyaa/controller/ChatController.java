package org.practice.seeyaa.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import org.springframework.stereotype.Component;

@Component
public class ChatController {
    @FXML
    private Button send;

    @FXML
    private TextArea wroteText;

    private String emailBy;
    private String emailTo;

    public void setInfo(String emailBy, String emailTo) {
        this.emailBy = emailBy;
        this.emailTo = emailTo;
    }
}
