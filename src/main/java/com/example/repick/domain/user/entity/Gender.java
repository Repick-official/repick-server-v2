package com.example.repick.domain.user.entity;

public enum Gender {
    MALE, FEMALE;

    public static Gender fromNaverInfo(String naverGender) {
        switch (naverGender) {
            case "M":
                return MALE;
            case "F":
                return FEMALE;
    }
        return null;
    }
    public static Gender fromKakaoInfo(String kakaoGender) {
        switch (kakaoGender) {
            case "male":
                return MALE;
            case "female":
                return FEMALE;
        }
        return null;
    }
    public static Gender fromGoogleInfo(String googleGender) {
        switch (googleGender) {
            case "male":
                return MALE;
            case "female":
                return FEMALE;
        }
        return null;
    }
}