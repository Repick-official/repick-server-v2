package com.example.repick.domain.clothingSales.scheduler;

import com.example.repick.domain.clothingSales.entity.BagCollect;
import com.example.repick.domain.clothingSales.repository.BagCollectRepository;
import com.example.repick.global.aws.PushNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component @RequiredArgsConstructor
public class ClothingSalesScheduler {

    private final BagCollectRepository bagCollectRepository;
    private final PushNotificationService pushNotificationService;

    @Scheduled(cron = "0 0 5 * * *", zone = "Asia/Seoul")
    public void checkNotProcessedBagInit() {
        List<BagCollect> bagCollects = bagCollectRepository.findNotProcessedBagCollects();

        bagCollects.forEach(bagCollect -> {
            int currentNotifyCount = bagCollect.getNotifyCount() + 1;

            if (currentNotifyCount == 1) {
                pushNotificationService.sendPushNotification(bagCollect.getUser().getId(), "리픽백 수거를 진행해 보세요!", "지금 바로 진행해 볼까요?");
            } else if (currentNotifyCount == 7) {
                currentNotifyCount = 0;
            }

            bagCollect.updateNotifyCount(currentNotifyCount);

        });
    }
}
