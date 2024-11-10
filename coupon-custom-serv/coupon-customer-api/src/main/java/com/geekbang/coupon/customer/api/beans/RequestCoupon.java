package com.geekbang.coupon.customer.api.beans;

import com.sun.istack.internal.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RequestCoupon {
    @NotNull
    private Long userId;
    @NotNull
    private Long couponTemplateId;
}
