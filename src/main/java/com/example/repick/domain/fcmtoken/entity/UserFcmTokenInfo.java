package com.example.repick.domain.fcmtoken.entity;


import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.*;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "userFcmInfo")
public class UserFcmTokenInfo {
    private Long userId;
    private String fcmToken;

    @DynamoDBHashKey
    public Long getUserId() {
        return userId;
    }

    @DynamoDBAttribute
    public String getFcmToken() {
        return fcmToken;
    }

    public void update(String fcmToken) {
        this.fcmToken = fcmToken == null ? this.fcmToken : fcmToken;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
