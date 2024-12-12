package org.practice.seeyaa.enums;

import lombok.Getter;

@Getter
public enum TypeOfLetter {
    INBOXES("inboxes"),
    SPAM("spam"),
    SENT("sent"),
    GARBAGE("garbage");
    private final String name;

    TypeOfLetter(String name) {
        this.name = name;
    }

}
