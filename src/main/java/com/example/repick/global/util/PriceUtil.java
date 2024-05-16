package com.example.repick.global.util;

public class PriceUtil {

    public static Long calculateDiscountPrice(Long price, Long discountRate) {
        return Math.round(price * (1 - discountRate / 100.0));
    }

    public static Long calculateDiscountRate(Long predictPrice, Long discountPrice) {
        return (predictPrice - discountPrice) * 100 / predictPrice;
    }

}
