package com.geekbang.coupon.template.dao.entity;

import com.geekbang.coupon.template.api.beans.rules.TemplateRule;
import com.geekbang.coupon.template.api.enums.CouponType;
import com.geekbang.coupon.template.dao.converter.CouponTypeConverter;
import com.geekbang.coupon.template.dao.converter.RuleConverter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

public class CouponTemplate implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "available", nullable = false)
    private Boolean available;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "shop_id")
    private Long shopId;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "type", nullable = false)
    @Converter(converter = CouponTypeConverter.class)
    private CouponType category;

    @CreatedDate
    @Column(name = "created_time", nullable = false)
    private Date createdTime;

    @Column(name = "rule", nullable = false)
    @Converter(converter = RuleConverter.class)
    private TemplateRule rule;
}
