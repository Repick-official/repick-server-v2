package com.example.repick.domain.announcement.service;

import com.example.repick.domain.announcement.dto.GetAnnouncement;
import com.example.repick.domain.announcement.dto.PostAnnouncement;
import com.example.repick.dynamodb.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;

    public Boolean createAnnouncement(PostAnnouncement postAnnouncement) {

        announcementRepository.save(postAnnouncement.toEntity());
        return true;

    }

    public List<GetAnnouncement> getAnnouncements() {
        return GetAnnouncement.listOf(announcementRepository.findAll());
    }

    public Boolean deleteAnnouncement(String announcementId) {
        announcementRepository.deleteById(announcementId);
        return true;
    }
}
