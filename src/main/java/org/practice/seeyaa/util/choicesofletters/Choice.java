package org.practice.seeyaa.util.choicesofletters;

import org.practice.seeyaa.enums.TypeOfLetter;
import org.practice.seeyaa.models.dto.LetterDto;

import java.util.List;

public interface Choice {
    List<LetterDto> addToBox(int index, String email);

    TypeOfLetter getChoice();
}
