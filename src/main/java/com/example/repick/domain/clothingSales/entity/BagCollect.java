package com.example.repick.domain.clothingSales.entity;

import com.example.repick.global.entity.Address;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class BagCollect extends ClothingSales{
    @Embedded
    private Address initAddress;
    @Column(name = "bag_quantity")
    private Integer bagQuantity;

    @Column(name = "notify_count")
    private int notifyCount = 0;

    public void updateNotifyCount(int notifyCount) {
        this.notifyCount = notifyCount;
    }
}
