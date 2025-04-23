package org.practice.seeyaa.configuration.movedletterconf;

import org.practice.seeyaa.enums.TypeOfLetter;

public sealed interface MovedLetterConfiguration permits MovedLetterConfigurationImpl {
    void setLetterType(String letterId, String email, TypeOfLetter type);
}
