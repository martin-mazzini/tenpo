package com.example.tenpo.testutils;

public class AssertionUtils {

    public static boolean isNumeric(String contentAsString) {
        try {
            Integer.valueOf(contentAsString);
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }
}
