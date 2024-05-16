package com.example.repick.global.util;

import java.math.BigDecimal;

public class PriceUtil {

    public static BigDecimal calculateDiscountPrice(Long price, Long discountRate) {
        return BigDecimal.valueOf(price * (1 - discountRate / 100.0));
    }

    public static BigDecimal calculatePredictPriceDiscountRate(Long predictPrice, Long price, Long discountRate) {
        return BigDecimal.valueOf(predictPrice / price * (1 - discountRate / 100.0) * 100);
    }
}
