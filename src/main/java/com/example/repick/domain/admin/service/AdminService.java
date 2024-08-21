package com.example.repick.domain.admin.service;

import com.example.repick.domain.admin.dto.DeliveryTrackerCallback;
import com.example.repick.domain.admin.dto.GetPresignedUrl;
import com.example.repick.domain.admin.dto.PostFcmToken;
import com.example.repick.domain.admin.entity.FileType;
import com.example.repick.domain.fcmtoken.entity.UserFcmTokenInfo;
import com.example.repick.domain.fcmtoken.service.UserFcmTokenInfoService;
import com.example.repick.global.error.exception.CustomException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import static com.example.repick.global.error.exception.ErrorCode.INVALID_REQUEST_ERROR;
import static com.example.repick.global.error.exception.ErrorCode.LAMBDA_INVOKE_FAILED;

@Service @RequiredArgsConstructor
public class AdminService {

    private final UserFcmTokenInfoService userFcmTokenInfoService;
    @Value("${delivery.clientId}")
    private String clientId;
    @Value("${delivery.clientSecret}")
    private String clientSecret;

    public UserFcmTokenInfo getFcmTokenByUserId(Long userId) {
        return userFcmTokenInfoService.getFcmToken(userId);
    }

    public Boolean saveFcmToken(PostFcmToken postFcmToken) {
        userFcmTokenInfoService.save(postFcmToken.userId(), postFcmToken.fcmToken());
        return true;
    }

    public Boolean saveOrUpdateFcmToken(PostFcmToken postFcmToken) {
        userFcmTokenInfoService.saveOrUpdate(postFcmToken.userId(), postFcmToken.fcmToken());
        return true;
    }

    public Boolean deleteFcmTokenByUserId(Long userId) {
        userFcmTokenInfoService.delete(userId);
        return true;
    }

    public GetPresignedUrl createPresignedUrl(String filename, FileType fileType) {

        LambdaClient awsLambda = LambdaClient.builder()
                .region(Region.AP_NORTHEAST_2)
                .build();

        InvokeResponse res;

        if (fileType == FileType.IMAGE) {
            String userId = filename.split("-")[0];
            filename = "images/" + userId + '/' + filename;
        } else if (fileType == FileType.EXCEL) {
            filename = "excels/" + System.currentTimeMillis() + filename;
        } else {
            throw new CustomException(INVALID_REQUEST_ERROR);
        }

        try {
            String json = String.format("{\"queryStringParameters\": {\"object_name\": \"%s\", \"operation\": \"upload\"}}", filename);

            SdkBytes payload = SdkBytes.fromUtf8String(json);

            //Setup an InvokeRequest
            InvokeRequest request = InvokeRequest.builder()
                    .functionName("lambda_presigned-url-generator")
                    .payload(payload)
                    .build();

            res = awsLambda.invoke(request);
            String value = res.payload().asUtf8String();

            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode rootNode = objectMapper.readTree(value);

            String body = rootNode.path("body").asText();
            JsonNode bodyNode = objectMapper.readTree(body);

            String presignedUrl = bodyNode.path("presigned_url").asText();

            return new GetPresignedUrl(presignedUrl);

        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new CustomException(LAMBDA_INVOKE_FAILED);
        }
    }

    public Boolean enableTracking(String trackingNumber, String carrierId, String callbackUrl) {
        WebClient webClient = WebClient.builder().build();
        String url = "https://apis.tracker.delivery/graphql";

        String expirationTimeFormatted = OffsetDateTime.now().plusDays(2).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        String requestBody = "{"
                + "\"query\":\"mutation RegisterTrackWebhook($input: RegisterTrackWebhookInput!) { registerTrackWebhook(input: $input) }\","
                + "\"variables\": {"
                + "    \"input\": {"
                + "        \"carrierId\": \"" + carrierId + "\","
                + "        \"trackingNumber\": \"" + trackingNumber + "\","
                + "        \"callbackUrl\": \"" + callbackUrl + "\","
                + "        \"expirationTime\": \"" + expirationTimeFormatted + "\""
                + "    }"
                + "  }"
                + "}";

        String response = webClient.post()
                .uri(url)
                .header("Content-Type", "application/json")
                .header("Authorization", "TRACKQL-API-KEY " + clientId + ":" + clientSecret)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return response != null && response.contains("\"registerTrackWebhook\":true");

    }

    public Boolean deliveryTrackingCallback(DeliveryTrackerCallback deliveryTrackerCallback) {
        System.out.println("deliveryTrackerCallback = " + deliveryTrackerCallback.carrierId());
        System.out.println("deliveryTrackerCallback = " + deliveryTrackerCallback.trackingNumber());

        return true;
    }
}
