package org.practice.seeyaa.util.fieldvalidation;

import io.micrometer.common.util.StringUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.Month;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FieldUtil {
    public static boolean isValid(String parameter) {
        return StringUtils.isNotBlank(parameter);
    }

    public static <E extends Enum<E>> boolean isValid(E value) {
        return value != null;
    }

    public static boolean isValid(Integer val) {
        return val != null && val > 0;
    }

    public static String refractorDate(LocalDateTime date) {
        if (date.getDayOfYear() == LocalDateTime.now().getDayOfYear()
                && date.getDayOfMonth() == LocalDateTime.now().getDayOfMonth())
            return (date.getHour() < 10 ? "0" + date.getHour() : date.getHour()) + ":"
                    + (date.getMinute() < 10 ? "0" + date.getMinute() : date.getMinute());
        else return date.getDayOfMonth() + " " + Month.of(date.getMonthValue());
    }
}
