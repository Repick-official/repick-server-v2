package com.example.repick.pushnotification;

import com.example.repick.domain.clothingSales.entity.BagCollect;
import com.example.repick.domain.clothingSales.repository.BagCollectRepository;
import com.example.repick.domain.fcmtoken.entity.UserFcmTokenInfo;
import com.example.repick.dynamodb.UserFcmTokenInfoRepository;
import com.example.repick.global.aws.PushNotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

import java.util.List;

@SpringBootTest
public class PushNotificationServiceTest {

    @Autowired
    private UserFcmTokenInfoRepository userFcmTokenInfoRepository;

    @Autowired
    private BagCollectRepository bagCollectRepository;

    @Autowired
    private PushNotificationService pushNotificationService;

    @Test
    public void notificationTest() {
        LambdaClient awsLambda = LambdaClient.builder()
                .region(Region.AP_NORTHEAST_2)
                .build();

        String fcmToken = userFcmTokenInfoRepository.findById(3L)
                .map(UserFcmTokenInfo::getFcmToken)
                .orElseThrow();

        String json = String.format("{\"fcmToken\": \"%s\", \"title\": \"test\", \"body\": \"test\"}", fcmToken);

        SdkBytes payload = SdkBytes.fromUtf8String(json);

        InvokeRequest request = InvokeRequest.builder()
                .functionName("lambda_push_notification_single")
                .payload(payload)
                .build();

        InvokeResponse res = awsLambda.invoke(request);

    }

    @Test
    public void pushNotificationServiceCronTest() {
        List<BagCollect> bagCollects = bagCollectRepository.findNotProcessedBagCollects();

        bagCollects.forEach(bagCollect -> {
            int currentNotifyCount = bagCollect.getNotifyCount() + 1;

            if (currentNotifyCount == 1) {
                pushNotificationService.sendPushNotification(bagCollect.getUser().getId(), "리픽백 수거를 진행해 보세요!", "지금 바로 진행해 볼까요?");
            } else if (currentNotifyCount == 7) {
                currentNotifyCount = 0;
            }

            bagCollect.updateNotifyCount(currentNotifyCount);

            bagCollectRepository.save(bagCollect);

        });

    }

}
