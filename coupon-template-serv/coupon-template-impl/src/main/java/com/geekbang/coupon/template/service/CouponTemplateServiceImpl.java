package com.geekbang.coupon.template.service;

import com.geekbang.coupon.template.api.beans.CouponTemplateInfo;
import com.geekbang.coupon.template.api.beans.PagedCouponTemplateInfo;
import com.geekbang.coupon.template.api.beans.TemplateSearchParams;
import com.geekbang.coupon.template.dao.CouponTemplateDao;
import com.geekbang.coupon.template.dao.entity.CouponTemplate;
import com.geekbang.coupon.template.converter.CouponTemplateConverter;
import com.geekbang.coupon.template.service.intf.CouponTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CouponTemplateServiceImpl implements CouponTemplateService {
    @Autowired
    private CouponTemplateDao templateDao;

    private static int MAX_TEMPLATE_COUNT_THRESHOLD = 100;

    @Override
    public CouponTemplateInfo cloneTemplate(Long templateId) {
        log.info("cloning template id: {}", templateId);
        CouponTemplate source = templateDao.findById(templateId).orElseThrow(() -> new IllegalArgumentException("invalid template ID"));

        CouponTemplate target = new CouponTemplate();
        BeanUtils.copyProperties(source, target);

        target.setAvailable(true);
        target.setId(null);

        templateDao.save(target);
        return CouponTemplateConverter.convertToTemplateInfo(target);
    }

    @Override
    public CouponTemplateInfo createTemplate(CouponTemplateInfo request) {
        if (request.getShopId() != null) {
            Integer count = templateDao.countByShopIdAndAvailable(request.getShopId(), true);
            if (count >= MAX_TEMPLATE_COUNT_THRESHOLD) {
                log.error("the totals of coupon template exceeds maximum number");
                throw new UnsupportedOperationException("exceed the maximum of coupon templates that you can create");
            }
        }

        CouponTemplate template = CouponTemplate.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getType())
                .available(true)
                .shopId(request.getShopId())
                .rule(request.getTemplateRule())
                .build();
        template = templateDao.save(template);

        return CouponTemplateConverter.convertToTemplateInfo(template);
    }

    @Override
    public PagedCouponTemplateInfo search(TemplateSearchParams request) {
        CouponTemplate example = CouponTemplate.builder()
                .shopId(request.getShopId())
                .category(request.getType())
                .available(request.getAvailable())
                .name(request.getName())
                .build();
        Pageable page = PageRequest.of(request.getPage(), request.getPageSize());
        Page<CouponTemplate> result = templateDao.findAll(Example.of(example), page);
        List<CouponTemplateInfo> couponTemplateInfoList = result.stream().map(CouponTemplateConverter::convertToTemplateInfo)
                .collect(Collectors.toList());

        PagedCouponTemplateInfo response = PagedCouponTemplateInfo.builder()
                .templateInfoList(couponTemplateInfoList)
                .page(request.getPage())
                .total(result.getTotalElements())
                .build();
        return response;
    }

    public List<CouponTemplateInfo> searchTemplate(CouponTemplateInfo request) {
        CouponTemplate example = CouponTemplate.builder()
                .shopId(request.getShopId())
                .category(request.getType())
                .available(request.getAvailable())
                .name(request.getName())
                .build();
        List<CouponTemplate> result = templateDao.findAll(Example.of(example));
        return result.stream().map(CouponTemplateConverter::convertToTemplateInfo)
                .collect(Collectors.toList());
    }

    @Override
    public CouponTemplateInfo loadTemplateInfo(Long id) {
        Optional<CouponTemplate> template = templateDao.findById(id);
        return template.map(CouponTemplateConverter::convertToTemplateInfo).orElse(null);
    }

    @Override
    @Transactional
    public void deleteTemplate(Long id) {
        int rows = templateDao.makeCouponUnavailable(id);
        if (rows == 0) {
            throw new IllegalArgumentException("Template Not Found: " + id);
        }
    }

    @Override
    public Map<Long, CouponTemplateInfo> getTemplateInfoMap(Collection<Long> ids) {
        List<CouponTemplate> templates = templateDao.findAllById(ids);
        return templates.stream().map(CouponTemplateConverter::convertToTemplateInfo)
                .collect(Collectors.toMap(CouponTemplateInfo::getId, Function.identity()));
    }

}
