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
public class SearchCoupon {
    @NotNull
    private Long userId;
    private Long shopId;
    private Integer couponStatus;
}
