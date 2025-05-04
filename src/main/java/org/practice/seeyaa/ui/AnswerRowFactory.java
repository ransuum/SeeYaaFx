package org.practice.seeyaa.ui;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.practice.seeyaa.models.dto.AnswerDto;

import java.util.function.Consumer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnswerRowFactory {
    public static HBox createAnswerRow(AnswerDto answerDto, Consumer<String> textSetter) {
        final var answerRow = new HBox();
        answerRow.getStyleClass().add("answer-row");
        answerRow.setSpacing(10);
        answerRow.setAlignment(Pos.CENTER_LEFT);

        final var avatar = FileUiUtils.createAvatar(answerDto.userByAnswered().firstname());

        final var contentBox = new VBox(5);
        contentBox.getStyleClass().add("answer-content");
        HBox.setHgrow(contentBox, Priority.ALWAYS);

        final var nameLabel = new Label(answerDto.userByAnswered().firstname());
        nameLabel.getStyleClass().add("answer-name");

        final var dateLabel = new Label(answerDto.createdAt().toString());
        dateLabel.getStyleClass().add("answer-date");

        final var headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.getChildren().addAll(nameLabel, dateLabel);

        contentBox.getChildren().addAll(headerBox);

        answerRow.getChildren().addAll(avatar, contentBox);

        answerRow.setOnMouseClicked(event -> {
            textSetter.accept(answerDto.answerText());
            var textFade = new FadeTransition(Duration.millis(300), (Node) textSetter);
            textFade.setFromValue(0.5);
            textFade.setToValue(1);
            textFade.play();
        });

        return answerRow;
    }
}
