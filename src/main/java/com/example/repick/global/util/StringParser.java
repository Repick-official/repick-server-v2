package com.example.repick.global.util;

public class StringParser {

    public static String parsePhoneNumber(String phoneNumber) {
        return phoneNumber.replaceAll("[^0-9]", "");
    }
}
