package com.geekbang.coupon.calculation.api.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Product {
    private Long productId;
    private long price;
    private Integer count;
    private Long shopId;
}
