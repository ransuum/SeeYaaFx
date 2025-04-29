package org.practice.seeyaa.ui;

import javafx.scene.Cursor;
import javafx.scene.control.TextField;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.practice.seeyaa.models.dto.LetterDto;

import static org.practice.seeyaa.util.fieldvalidation.FieldUtil.refractorField;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LetterUIFactory {
    public static TextField createTextField(LetterDto letter, int function) {
        final var textField = new TextField();
        textField.setCursor(Cursor.HAND);
        textField.setPrefWidth(600);
        textField.setId(letter.id());

        final var byName = (function == 1)
                ? refractorField(25, "By:", letter.userBy().firstname(), letter.userBy().lastname())
                : refractorField(25, "To:", letter.userTo().firstname(), letter.userTo().lastname());

        final var paddedTopic = refractorField(30, letter.topic());

        textField.setText(byName + paddedTopic);
        textField.setEditable(false);
        return textField;
    }
}
