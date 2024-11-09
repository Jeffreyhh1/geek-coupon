package com.geekbang.coupon.calculation.template;

import com.geekbang.coupon.calculation.api.beans.ShoppingCart;
import com.geekbang.coupon.calculation.template.impl.*;
import com.geekbang.coupon.template.api.beans.CouponTemplateInfo;
import com.geekbang.coupon.template.api.enums.CouponType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;

@Component
public class CouponTemplateFactory {
    @Autowired
    private MoneyOffTemplate moneyOffTemplate;
    @Autowired
    private DiscountTemplate discountTemplate;
    @Autowired
    private RandomReductionTemplate randomReductionTemplate;
    @Autowired
    private LonelyNightTemplate lonelyNightTemplate;
    @Autowired
    private DummyTemplate dummyTemplate;

    public RuleTemplate getTemplate(ShoppingCart order) {
        if (CollectionUtils.isEmpty(order.getCouponInfoList())) {
            return dummyTemplate;
        }

        CouponTemplateInfo templateInfo = order.getCouponInfoList().get(0).getTemplateInfo();
        CouponType category = templateInfo.getType();

        switch (category) {
            case MONEY_OFF:
                return moneyOffTemplate;
            case RANDOM_DISCOUNT:
                return randomReductionTemplate;
            case DISCOUNT:
                return discountTemplate;
            case LONELY_NIGHT_MONEY_OFF:
                return lonelyNightTemplate;
            default:
                return dummyTemplate;
        }
    }
}
