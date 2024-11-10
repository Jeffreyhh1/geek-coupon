package com.geekbang.coupon.customer.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public enum CouponStatus {
    AVAILABLE("未使用", 1),
    USED("已用", 2),
    INACTIVE("已经注销", 3);

    private String description;
    private Integer code;

    public static CouponStatus convert(Integer code) {
        if (code == null) {
            return null;
        }
        return Stream.of(values()).filter(bean -> bean.getCode().equals(code)).findAny().orElse(null);
    }
}
