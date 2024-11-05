package com.geekbang.coupon.template.converter;

import com.geekbang.coupon.template.api.beans.CouponTemplateInfo;
import com.geekbang.coupon.template.dao.entity.CouponTemplate;


public class CouponTemplateConverter {
    public static CouponTemplateInfo convertToTemplateInfo(CouponTemplate template) {
        return CouponTemplateInfo.builder()
                .id(template.getId())
                .name(template.getName())
                .description(template.getDescription())
                .type(template.getCategory())
                .shopId(template.getShopId())
                .available(template.getAvailable())
                .templateRule(template.getRule()).build();
    }
}
