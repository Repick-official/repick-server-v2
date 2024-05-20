package com.example.repick.domain.recommendation.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@DynamoDBTable(tableName = "userPreference")
public class UserPreference {
    private Long userId;
    private List<Long> categoryPreference;
    private List<Long> stylePreference;

    @DynamoDBHashKey
    public Long getUserId() {
        return userId;
    }

    public UserPreference(Long userId) {
        this.userId = userId;

        List<Long> categoryPreference = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            categoryPreference.add(0L);
        }
        this.categoryPreference = categoryPreference;

        this.stylePreference = List.of(0L, 0L, 0L);
    }

}
