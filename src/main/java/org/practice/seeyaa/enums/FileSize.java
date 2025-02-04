package org.practice.seeyaa.enums;

import lombok.Getter;

public enum FileSize {
    TEN_MB(10 * 1024 * 1024),
    THIRTY_MB(30 * 1024 * 1024),
    FIFTY_MB(50 * 1024 * 1024),
    ONE_HUNDRED_MB(100 * 1024 * 1024),
    TWO_HUNDRED_MB(200 * 1024 * 1024),
    THREE_HUNDRED_MB(300 * 1024 * 1024),
    FOUR_HUNDRED_MB(400 * 1024 * 1024),
    FIVE_HUNDRED_MB(500 * 1024 * 1024),
    SIX_HUNDRED_MB(600 * 1024 * 1024),
    SEVEN_HUNDRED_MB(700 * 1024 * 1024),
    EIGHT_HUNDRED_MB(800 * 1024 * 1024),
    NINE_HUNDRED_MB(900 * 1024 * 1024),
    ONE_GB(1000 * 1024 * 1024);

    @Getter
    private final int size;

    FileSize(int size) {
        this.size = size;
    }
}
