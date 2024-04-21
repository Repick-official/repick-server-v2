package com.example.repick.global.util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeParser {

    public static LocalDate toLocalDate(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public static String toTimeAgo(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(dateTime, now);

        long days = duration.toDays();
        if (days >= 30) {
            return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        if (days >= 1) {
            return days + "일 전";
        }

        long hours = duration.toHours();
        if (hours >= 1) {
            return hours + "시간 전";
        }

        long minutes = duration.toMinutes();
        if (minutes >= 1) {
            return minutes + "분 전";
        }

        return "방금 전";
    }

    public static String toString(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd");
        return dateTime.format(formatter);
    }


}
