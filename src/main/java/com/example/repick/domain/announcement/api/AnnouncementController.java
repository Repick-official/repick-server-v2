package com.example.repick.domain.announcement.api;

import com.example.repick.domain.announcement.dto.GetAnnouncement;
import com.example.repick.domain.announcement.dto.PostAnnouncement;
import com.example.repick.domain.announcement.service.AnnouncementService;
import com.example.repick.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Announcement", description = "서버쪽 API입니다. 필요한 경우 푸시 알림 테스트 용도로 사용 가능합니다.")
@RestController @RequestMapping("/announcement") @RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @Operation(summary = "공지사항 등록하기",
            description = "공지사항을 등록합니다. 등록 시 모든 유저에게 푸시 알림이 전송됩니다.")
    @PostMapping
    public SuccessResponse<Boolean> saveAnnouncement(@RequestBody PostAnnouncement postAnnouncement) {
        return SuccessResponse.success(announcementService.createAnnouncement(postAnnouncement));
    }

    @Operation(summary = "공지사항 삭제하기")
    @DeleteMapping("/{announcementId}")
    public SuccessResponse<Boolean> deleteAnnouncement(@PathVariable String announcementId) {
        return SuccessResponse.success(announcementService.deleteAnnouncement(announcementId));
    }

    @Operation(summary = "공지사항 조회하기")
    @GetMapping
    public SuccessResponse<List<GetAnnouncement>> getAnnouncement() {
        return SuccessResponse.success(announcementService.getAnnouncements());
    }
}

