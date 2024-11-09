package com.geekbang.coupon.calculation.api.beans;

import com.geekbang.coupon.template.api.beans.CouponInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SimulationOrder {
    @NotEmpty
    private List<Product> productList;
    @NotEmpty
    private List<Long> couponIdList;
    private List<CouponInfo> couponInfoList;
    @NotNull
    private Long userId;
}
