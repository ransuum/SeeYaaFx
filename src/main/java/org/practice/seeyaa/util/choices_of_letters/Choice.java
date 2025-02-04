package org.practice.seeyaa.util.choices_of_letters;

import javafx.scene.layout.VBox;
import org.practice.seeyaa.enums.TypeOfLetter;
import org.practice.seeyaa.models.dto.LetterDto;

import java.util.List;

public interface Choice {
    List<LetterDto> addToBox(int index, String email);
    TypeOfLetter getChoice();
}
