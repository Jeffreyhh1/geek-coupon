package com.geekbang.coupon.template.api.beans;

import com.geekbang.coupon.template.api.beans.rules.TemplateRule;
import com.geekbang.coupon.template.api.enums.CouponType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CouponTemplateInfo {
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String description;
    // Note(zhangjianhui03): use enum rather than String
    @NotNull
    private CouponType type;
    private Long shopId;
    @NotNull
    private TemplateRule templateRule;
    private Boolean available;
}
