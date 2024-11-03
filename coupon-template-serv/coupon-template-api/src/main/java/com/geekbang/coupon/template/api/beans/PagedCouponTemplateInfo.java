package com.geekbang.coupon.template.api.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PagedCouponTemplateInfo {
    private List<CouponTemplateInfo> templateInfoList;
    private int page;
    private long total;
}
