package com.geekbang.coupon.template.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum CouponType {
    UNKNOWN("unknown", "0"),
    MONEY_OFF("money_off", "1"),
    DISCOUNT("discount", "2"),
    RANDOM_DISCOUNT("random_discount", "3"),
    LONELY_NIGHT_MONEY_OFF("lonely_night_money_off", "4"),
    ANTI_PUA("anti_pua", "5");

    private String description;
    private String code;

    // NOTE(zhangjianhui03): Constructor for enum, rather than @AllArgsConstructor
    CouponType(String description, String code) {
        this.description = description;
        this.code = code;
    }

    public static CouponType convert(String code) {
        return Stream.of(values()).filter(couponType -> couponType.code.equalsIgnoreCase(code)).findFirst().orElse(UNKNOWN);
    }
}
