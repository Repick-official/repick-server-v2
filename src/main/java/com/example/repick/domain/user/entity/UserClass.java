package com.example.repick.domain.user.entity;

public enum UserClass {

    ROOKIE_COLLECTOR(1, "TEST_BRONZE"),
    BEGINNER_COLLECTOR(2, "TEST_SILVER"),
    PRO_COLLECTOR(3, "TEST_GOLD");

    private final int id;
    private final String value;

    UserClass(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

}
