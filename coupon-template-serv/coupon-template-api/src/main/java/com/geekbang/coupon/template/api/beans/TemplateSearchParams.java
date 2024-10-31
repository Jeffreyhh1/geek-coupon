package com.geekbang.coupon.template.api.beans;

import com.geekbang.coupon.template.api.enums.CouponType;

public class TemplateSearchParams {
    private Long id;
    private String name;
    private CouponType type;
    private Long shopId;
    private Boolean available;
    private int page;
    private int pageSize;
}
