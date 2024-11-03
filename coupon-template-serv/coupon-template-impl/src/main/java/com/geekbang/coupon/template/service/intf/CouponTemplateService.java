package com.geekbang.coupon.template.service.intf;

import com.geekbang.coupon.template.api.beans.CouponTemplateInfo;
import com.geekbang.coupon.template.api.beans.PagedCouponTemplateInfo;
import com.geekbang.coupon.template.api.beans.TemplateSearchParams;

import java.util.Collection;
import java.util.Map;

public interface CouponTemplateService {

    CouponTemplateInfo createTemplate(CouponTemplateInfo request);

    CouponTemplateInfo cloneTemplate(Long templateId);

    PagedCouponTemplateInfo search(TemplateSearchParams request);

    CouponTemplateInfo loadTemplateInfo(Long id);

    void deleteTemplate(Long id);

    Map<Long, CouponTemplateInfo> getTemplateInfoMap(Collection<Long> ids);
}
