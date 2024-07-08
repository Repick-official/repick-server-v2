package com.example.repick.domain.admin.service;

import com.example.repick.domain.admin.dto.GetPresignedUrl;
import com.example.repick.domain.admin.dto.PostFcmToken;
import com.example.repick.domain.fcmtoken.entity.UserFcmTokenInfo;
import com.example.repick.domain.fcmtoken.service.UserFcmTokenInfoService;
import com.example.repick.global.error.exception.CustomException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

import static com.example.repick.global.error.exception.ErrorCode.LAMBDA_INVOKE_FAILED;

@Service @RequiredArgsConstructor
public class AdminService {

    private final UserFcmTokenInfoService userFcmTokenInfoService;

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

    public GetPresignedUrl createPresignedUrl(String filename) {

        LambdaClient awsLambda = LambdaClient.builder()
                .region(Region.AP_NORTHEAST_2)
                .build();

        InvokeResponse res;

        filename = filename + "-" + System.currentTimeMillis();

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
}
