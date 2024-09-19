package org.practice.seeyaa.util.dateCheck;

public class StringCheck {

    public static boolean checkStringParametrs(String parametr){
        return parametr != null && !parametr.isEmpty() && !parametr.isBlank();
    }
}
