package org.practice.seeyaa.util.datecheck;

public class StringCheck {
    public static boolean checkStringParameters(String parameter){
        return parameter != null && !parameter.trim().isEmpty() && !parameter.trim().isBlank();
    }
}
