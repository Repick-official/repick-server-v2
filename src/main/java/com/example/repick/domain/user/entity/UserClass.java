package com.example.repick.domain.user.entity;

public enum UserClass {

    ROOKIE_COLLECTOR(1, "루키콜렉터"),
    BEGINNER_COLLECTOR(2, "초보콜렉터"),
    PRO_COLLECTOR(3, "프로콜렉터");

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
