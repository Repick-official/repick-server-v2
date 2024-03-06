package com.example.repick.global.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class Address {
    private String postalCode;
    private String mainAddress;
    private String detailAddress;

    public Address(String postalCode, String mainAddress, String detailAddress) {
        this.postalCode = postalCode;
        this.mainAddress = mainAddress;
        this.detailAddress = detailAddress;
    }

    protected Address() {
    }
}
