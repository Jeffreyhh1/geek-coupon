package com.geekbang.coupon.calculation.api.beans;

import com.google.common.collect.Maps;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
@Data
public class SimulationResponse {
    private Long bestCouponId;
    private Map<Long, Long> couponToOrderPrice = Maps.newHashMap();
}
