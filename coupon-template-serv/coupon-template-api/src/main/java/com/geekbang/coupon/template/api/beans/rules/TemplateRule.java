package com.geekbang.coupon.template.api.beans.rules;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TemplateRule {
    private Discount discount;
    private Integer limitation;
    private Long deadLine;
}
