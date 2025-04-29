package org.practice.seeyaa.configuration.movedletterconf;

import org.practice.seeyaa.enums.TypeOfLetter;

@FunctionalInterface
public interface MovedLetterConfiguration {
    void setLetterType(String letterId, String email, TypeOfLetter type);
}
