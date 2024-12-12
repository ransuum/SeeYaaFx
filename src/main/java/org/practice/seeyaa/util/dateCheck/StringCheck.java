package org.practice.seeyaa.util.dateCheck;

public class StringCheck {
    public static boolean checkStringParameters(String parameter){
        return parameter != null && !parameter.isEmpty() && !parameter.isBlank();
    }
}
