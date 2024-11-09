package com.geekbang.coupon.calculation.template;

import com.geekbang.coupon.calculation.api.beans.Product;
import com.geekbang.coupon.calculation.api.beans.ShoppingCart;
import com.geekbang.coupon.template.api.beans.CouponTemplateInfo;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractRuleTemplate implements RuleTemplate {
    @Override
    public ShoppingCart calculate(ShoppingCart order) {
        Long orderTotalAmount = getTotalPrice(order.getProductList());
        Map<Long, Long> sumAmount = this.getTotalPriceGroupByShop(order.getProductList());
        CouponTemplateInfo templateInfo = order.getCouponInfoList().get(0).getTemplateInfo();
        Long threshold = templateInfo.getTemplateRule().getDiscount().getThreshold();
        Long quota = templateInfo.getTemplateRule().getDiscount().getQuota();
        Long shopId = templateInfo.getShopId();

        Long shopTotalAmount = (shopId == null) ? orderTotalAmount : sumAmount.get(shopId);

        if (shopTotalAmount == null || shopTotalAmount < threshold) {
            log.warn("Totals of amount not meet, ur coupons are not applicable to this order");
            order.setCost(orderTotalAmount);
            order.setCouponInfoList(Collections.emptyList());
            return order;
        }

        Long newCost = calculateNewPrice(orderTotalAmount, shopTotalAmount, quota);
        if (newCost < minCost()) {
            newCost = minCost();
        }
        order.setCost(newCost);
        log.debug("original price={}, new price={}", orderTotalAmount, newCost);
        return order;
    }

    abstract protected Long calculateNewPrice(Long orderTotalAmount, Long shopTotalAmount, Long quota);

    protected long getTotalPrice(List<Product> productList) {
        return productList.stream().mapToLong(product -> product.getPrice() * product.getCount()).sum();
    }

    protected Map<Long, Long> getTotalPriceGroupByShop(List<Product> productList) {
        return productList.stream().collect(Collectors.groupingBy(Product::getShopId, Collectors.summingLong(p -> p.getPrice() * p.getCount())));
    }

    protected long minCost() {
        return 1L;
    }

    protected long convertToDecimal(Double value) {
        return new BigDecimal(value).setScale(0, RoundingMode.HALF_UP).longValue();
    }

}
