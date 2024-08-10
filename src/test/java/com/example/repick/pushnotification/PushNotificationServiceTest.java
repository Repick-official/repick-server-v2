package com.example.repick.pushnotification;

import com.example.repick.domain.fcmtoken.entity.UserFcmTokenInfo;
import com.example.repick.dynamodb.UserFcmTokenInfoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

@SpringBootTest
public class PushNotificationServiceTest {

    @Autowired
    private UserFcmTokenInfoRepository userFcmTokenInfoRepository;

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

}
