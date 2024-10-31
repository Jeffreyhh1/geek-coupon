package com.geekbang.coupon.template.api.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CouponInfo {
    private Long id;
    private Long templateId;
    private Long userId;
    private Long shopId;
    private Integer status;
    private CouponTemplateInfo templateInfo;
}
