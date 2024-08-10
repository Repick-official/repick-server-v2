package com.example.repick.global.aws;

import com.example.repick.domain.fcmtoken.entity.UserFcmTokenInfo;
import com.example.repick.dynamodb.UserFcmTokenInfoRepository;
import com.example.repick.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

import static com.example.repick.global.error.exception.ErrorCode.USER_NOT_FOUND;

@Service @RequiredArgsConstructor
public class PushNotificationService {

    private final UserFcmTokenInfoRepository userFcmTokenInfoRepository;

    public void sendPushNotification(Long userId, String title, String body) {

        LambdaClient awsLambda = LambdaClient.builder()
                .region(Region.AP_NORTHEAST_2)
                .build();

        UserFcmTokenInfo userFcmTokenInfo = userFcmTokenInfoRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        String json = String.format("{\"fcmToken\": \"%s\", \"title\": \"%s\", \"body\": \"%s\"}", userFcmTokenInfo.getFcmToken(), title, body);

        SdkBytes payload = SdkBytes.fromUtf8String(json);

        InvokeRequest request = InvokeRequest.builder()
                .functionName("lambda_push_notification_single")
                .payload(payload)
                .build();

        InvokeResponse res = awsLambda.invoke(request);


    }

}
