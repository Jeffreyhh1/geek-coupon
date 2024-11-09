package com.geekbang.coupon.calculation.controller.service;

import com.alibaba.fastjson.JSON;
import com.geekbang.coupon.calculation.api.beans.ShoppingCart;
import com.geekbang.coupon.calculation.api.beans.SimulationOrder;
import com.geekbang.coupon.calculation.api.beans.SimulationResponse;
import com.geekbang.coupon.calculation.template.CouponTemplateFactory;
import com.geekbang.coupon.calculation.template.RuleTemplate;
import com.geekbang.coupon.template.api.beans.CouponInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@Slf4j
public class CouponCalculationServiceImpl implements CouponCalculationService {
    @Autowired
    private CouponTemplateFactory couponTemplateFactory;

    @Override
    public ShoppingCart calculateOrderPrice(@RequestBody ShoppingCart cart) {
        log.info("calculate order price: {}", JSON.toJSONString(cart));
        RuleTemplate ruleTemplate = couponTemplateFactory.getTemplate(cart);
        return ruleTemplate.calculate(cart);
    }

    @Override
    public SimulationResponse simulateOrder(@RequestBody SimulationOrder order) {
        SimulationResponse response = new SimulationResponse();
        Long minOrderPrice = Long.MAX_VALUE;

        for (CouponInfo coupon : order.getCouponInfoList()) {
            ShoppingCart cart = new ShoppingCart();
            cart.setProductList(order.getProductList());
            cart.setCouponInfoList(Lists.newArrayList(coupon));
            cart = couponTemplateFactory.getTemplate(cart).calculate(cart);

            Long couponId = coupon.getId();
            Long orderPrice = cart.getCost();
            response.getCouponToOrderPrice().put(couponId, orderPrice);

            if (minOrderPrice > orderPrice) {
                response.setBestCouponId(couponId);
                minOrderPrice = orderPrice;
            }
        }
        return response;
    }
}
