package com.example.repick.domain.announcement.dto;

import com.example.repick.domain.announcement.entity.Announcement;

import java.util.ArrayList;
import java.util.List;

public record GetAnnouncement(
        String id,
        String title,
        String content
) {

    public static List<GetAnnouncement> listOf(Iterable<Announcement> all) {
        List<GetAnnouncement> getAnnouncements = new ArrayList<>();

        for (Announcement announcement : all) {
            System.out.println("announcement = " + announcement.getId());
            getAnnouncements.add(new GetAnnouncement(
                    announcement.getId(),
                    announcement.getTitle(),
                    announcement.getContent()
            ));
        }

        return getAnnouncements;
    }
}
