package com.example.repick.dynamodb;

import com.example.repick.domain.announcement.entity.Announcement;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface AnnouncementRepository extends CrudRepository<Announcement, String> {
}
