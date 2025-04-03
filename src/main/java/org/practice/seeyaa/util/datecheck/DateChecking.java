package org.practice.seeyaa.util.datecheck;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.Month;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DateChecking {

    public static String checkDate(LocalDateTime date) {
        if (date.getDayOfYear() == LocalDateTime.now().getDayOfYear()
                && date.getDayOfMonth() == LocalDateTime.now().getDayOfMonth())
            return (date.getHour() < 10 ? "0" + date.getHour() : date.getHour()) + ":"
                    + (date.getMinute() < 10 ? "0" + date.getMinute() : date.getMinute());
        else return date.getDayOfMonth() + " " + Month.of(date.getMonthValue());
    }
}
