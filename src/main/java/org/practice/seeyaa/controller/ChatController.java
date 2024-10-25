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
}
