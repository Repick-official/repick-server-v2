package com.example.repick.domain.announcement.dto;

import com.example.repick.domain.announcement.entity.Announcement;

public record PostAnnouncement(
        String title,
        String content
) {
    public Announcement toEntity() {
        return Announcement.of(title, content);
    }
}
