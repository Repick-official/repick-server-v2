package com.example.repick.domain.user.entity;

public enum Gender {
    MALE, FEMALE;

    public static Gender fromNaverInfo(String naverGender) {
        if (naverGender == null) {
            return null;
        }

        switch (naverGender) {
            case "M":
                return MALE;
            case "F":
                return FEMALE;
            default:
                return null;
        }
    }
    public static Gender fromKakaoInfo(String kakaoGender) {
        if (kakaoGender == null) {
            return null;
        }

        switch (kakaoGender) {
            case "male":
                return MALE;
            case "female":
                return FEMALE;
            default:
                return null;
        }
    }

    public static Gender fromGoogleInfo(String googleGender) {
        if (googleGender == null) {
            return null;
        }
        switch (googleGender) {
            case "male":
                return MALE;
            case "female":
                return FEMALE;
            default:
                return null;
        }
    }
}