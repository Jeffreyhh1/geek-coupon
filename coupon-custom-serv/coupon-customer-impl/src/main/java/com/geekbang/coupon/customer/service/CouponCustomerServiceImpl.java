package com.geekbang.coupon.customer.service;

import com.geekbang.coupon.calculation.api.beans.ShoppingCart;
import com.geekbang.coupon.calculation.api.beans.SimulationOrder;
import com.geekbang.coupon.calculation.api.beans.SimulationResponse;
import com.geekbang.coupon.customer.api.beans.RequestCoupon;
import com.geekbang.coupon.customer.api.beans.SearchCoupon;
import com.geekbang.coupon.customer.api.enums.CouponStatus;
import com.geekbang.coupon.customer.dao.CouponDao;
import com.geekbang.coupon.customer.dao.entity.Coupon;
import com.geekbang.coupon.template.api.beans.CouponInfo;
import com.geekbang.coupon.template.api.beans.CouponTemplateInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;

import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.geekbang.coupon.customer.constant.Constant.TRAFFIC_VERSION;

@Service
@Slf4j
public class CouponCustomerServiceImpl implements CouponCustomerService {
    @Autowired
    private CouponDao couponDao;
    @Autowired
    private WebClient.Builder webClientBuilder;

    @Override
    public SimulationResponse simulateOrderPrice(SimulationOrder order) {
        List<CouponInfo> couponInfoList = Lists.newArrayList();
        for (Long couponId : order.getCouponIdList()) {
            Coupon example = Coupon.builder().userId(order.getUserId()).id(couponId).status(CouponStatus.AVAILABLE).build();
            Optional<Coupon> couponOptional = couponDao.findAll(Example.of(example)).stream().findFirst();
            if (couponOptional.isPresent()) {
                Coupon coupon = couponOptional.get();
                CouponInfo couponInfo = CouponConverter.convertToCouponInfo(coupon);
                couponInfo.setTemplateInfo(loadTemplateInfo(coupon.getTemplateId()));
                couponInfoList.add(couponInfo);
            }
        }
        order.setCouponInfoList(couponInfoList);
//        return calculationService.simulateOrder(order);
        return webClientBuilder.build().post()
                .uri("http://coupon-calculation-serv/calculator/simulate")
                .bodyValue(order)
                .retrieve()
                .bodyToMono(SimulationResponse.class)
                .block();
    }

    @Override
    public List<CouponInfo> findCoupon(SearchCoupon request) {
        Coupon example = Coupon.builder().userId(request.getUserId()).status(CouponStatus.convert(request.getCouponStatus()))
                .shopId(request.getShopId())
                .build();
        List<Coupon> couponList = couponDao.findAll(Example.of(example));
        if (couponList.isEmpty()) {
            return Lists.newArrayList();
        }
        List<Long> templateIds = couponList.stream().map(Coupon::getTemplateId).collect(Collectors.toList());
//        Map<Long, CouponTemplateInfo> templateInfoMap = templateService.getTemplateInfoMap(templateIds);
        Map<Long, CouponTemplateInfo> templateInfoMap = webClientBuilder.build().get()
                        .uri("http://coupon-template-serv/template/getBatch?ids=" + templateIds)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<Long, CouponTemplateInfo>>() {}).block();
        couponList.forEach(e -> e.setTemplateInfo(templateInfoMap.get(e.getTemplateId())));
        return couponList.stream().map(CouponConverter::convertToCouponInfo).collect(Collectors.toList());

    }

    @Override
    public Coupon requestCoupon(RequestCoupon request) {
//        CouponTemplateInfo templateInfo = templateService.loadTemplateInfo(request.getCouponTemplateId());
        CouponTemplateInfo templateInfo = webClientBuilder.build().get()
                .uri("http://coupon-template-serv/template/getTemplate?id=" + request.getCouponTemplateId())
                .header(TRAFFIC_VERSION, request.getTrafficVersion())
                .retrieve()
                .bodyToMono(CouponTemplateInfo.class)
                .block();
        if (templateInfo == null) {
            log.error("invalid template id={}", request.getCouponTemplateId());
            throw new IllegalArgumentException("Invalid template id");
        }
        long now = Calendar.getInstance().getTimeInMillis();
        Long expireTime = templateInfo.getTemplateRule().getDeadLine();
        if (expireTime != null && now >= expireTime || BooleanUtils.isFalse(templateInfo.getAvailable())) {
            log.error("template is not available id={}", request.getCouponTemplateId());
            throw new IllegalArgumentException("template is unavailable");
        }
        long count = couponDao.countByUserIdAndTemplateId(request.getUserId(), request.getCouponTemplateId());
        if (count >= templateInfo.getTemplateRule().getLimitation()) {
            log.error("exceeds maximum number");
            throw new IllegalArgumentException("exceeds maximum number");
        }
        Coupon coupon = Coupon.builder()
                .templateId(request.getCouponTemplateId())
                .userId(request.getUserId())
                .shopId(templateInfo.getShopId())
                .status(CouponStatus.AVAILABLE)
                .build();
        couponDao.save(coupon);
        return coupon;
    }

    @Override
    @Transactional
    public ShoppingCart placeOrder(ShoppingCart order) {
        if (CollectionUtils.isEmpty(order.getProductList())) {
            log.error("invalid check out request, order={}", order);
            throw new IllegalArgumentException("cart if empty");
        }
        Coupon coupon = null;
        if (order.getCouponId() != null) {
            Coupon example = Coupon.builder()
                    .userId(order.getUserId())
                    .id(order.getCouponId())
                    .status(CouponStatus.AVAILABLE)
                    .build();
            coupon = couponDao.findAll(Example.of(example)).stream().findFirst().orElseThrow(() -> new RuntimeException("Coupon not found"));
            CouponInfo couponInfo = CouponConverter.convertToCouponInfo(coupon);
            couponInfo.setTemplateInfo(loadTemplateInfo(coupon.getTemplateId()));
            order.setCouponInfoList(Lists.newArrayList(couponInfo));
        }

//        ShoppingCart checkoutInfo = calculationService.calculateOrderPrice(order);
        ShoppingCart checkoutInfo = webClientBuilder.build().post()
                .uri("http://coupon-calculation-serv/calculator/checkout")
                .bodyValue(order)
                .retrieve()
                .bodyToMono(ShoppingCart.class)
                .block();
        if (coupon != null) {
            if (CollectionUtils.isEmpty(checkoutInfo.getCouponInfoList())) {
                log.error("cannot apply coupon to order, couponId={}", coupon.getId());
                throw new IllegalArgumentException("coupon is not applicable to this order");
            }
            log.info("update coupon status to used, couponId={}", coupon.getId());
            coupon.setStatus(CouponStatus.USED);
            couponDao.save(coupon);
        }
        return checkoutInfo;
    }

    @Override
    public void deleteCoupon(Long userId, Long couponId) {
        Coupon example = Coupon.builder()
                .userId(userId)
                .id(couponId)
                .status(CouponStatus.AVAILABLE)
                .build();
        Coupon coupon = couponDao.findAll(Example.of(example)).stream().findFirst().orElseThrow(() -> new RuntimeException("Could not find available coupon"));
        coupon.setStatus(CouponStatus.INACTIVE);
        couponDao.save(coupon);
    }

    private CouponTemplateInfo loadTemplateInfo(Long templateId) {
        return webClientBuilder.build().get()
                .uri("http://coupon-template-serv/template/getTemplate?id=" + templateId)
                .retrieve()
                .bodyToMono(CouponTemplateInfo.class)
                .block();
    }
}
