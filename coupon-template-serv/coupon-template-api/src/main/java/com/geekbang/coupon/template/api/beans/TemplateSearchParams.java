package com.geekbang.coupon.template.api.beans;

import com.geekbang.coupon.template.api.enums.CouponType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TemplateSearchParams {
    private Long id;
    private String name;
    private CouponType type;
    private Long shopId;
    private Boolean available;
    private int page;
    private int pageSize;
}
