package com.geekbang.coupon.customer.service;

import com.geekbang.coupon.calculation.api.beans.ShoppingCart;
import com.geekbang.coupon.calculation.api.beans.SimulationOrder;
import com.geekbang.coupon.calculation.api.beans.SimulationResponse;
import com.geekbang.coupon.customer.api.beans.RequestCoupon;
import com.geekbang.coupon.customer.api.beans.SearchCoupon;
import com.geekbang.coupon.customer.dao.entity.Coupon;
import com.geekbang.coupon.template.api.beans.CouponInfo;

import java.util.List;

public interface CouponCustomerService {
    Coupon requestCoupon(RequestCoupon request);

    ShoppingCart placeOrder(ShoppingCart info);

    SimulationResponse simulateOrderPrice(SimulationOrder order);

    void deleteCoupon(Long userId, Long couponId);

    List<CouponInfo> findCoupon(SearchCoupon request);
}
