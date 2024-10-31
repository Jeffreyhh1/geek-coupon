package com.geekbang.coupon.template.api.beans.rules;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Discount {
    private Long quota;
    // Note(zhangjianhui03): unit is cent
    private Long threshold;
}
