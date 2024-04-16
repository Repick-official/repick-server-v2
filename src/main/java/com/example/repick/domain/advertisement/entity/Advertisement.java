package com.example.repick.domain.advertisement.entity;

import com.example.repick.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity @NoArgsConstructor(access = AccessLevel.PROTECTED) @Getter
public class Advertisement extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String imageUrl;
    private String linkUrl;  // null일 시 클릭 불가 광고
    @Column(nullable = false, unique = true)
    private Integer sequence;

    @Builder
    public Advertisement(String imageUrl, String linkUrl, Integer sequence) {
        this.imageUrl = imageUrl;
        this.linkUrl = linkUrl;
        this.sequence = sequence;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
