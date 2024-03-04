package com.example.repick.domain.user.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @NoArgsConstructor
@DynamoDBTable(tableName = "userSmsVerificationInfo")
public class UserSmsVerificationInfo {

    private String id;
    private Long userId;
    private String phoneNumber;
    private String verificationCode;
    private Long expirationTime;

    @DynamoDBHashKey
    public String getId() {
        return id;
    }
    @DynamoDBAttribute
    public Long getUserId() {
        return userId;
    }
    @DynamoDBAttribute
    public String getPhoneNumber() {
        return phoneNumber;
    }
    @DynamoDBAttribute
    public String getVerificationCode() {
        return verificationCode;
    }
    @DynamoDBAttribute
    public Long getExpirationTime() {
        return expirationTime;
    }

    @Builder
    public UserSmsVerificationInfo(String id, Long userId, String phoneNumber, String verificationCode, Long expirationTime) {
        this.id = id;
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.verificationCode = verificationCode;
        this.expirationTime = expirationTime;
    }

}
