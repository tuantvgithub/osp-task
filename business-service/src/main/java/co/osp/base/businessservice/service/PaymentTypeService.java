package co.osp.base.businessservice.service;

import co.osp.base.businessservice.entity.PaymentType;

import java.util.List;

public interface PaymentTypeService {

    List<PaymentType> search();
    List<PaymentType> findByListIds(List<Long> ids);
    PaymentType findById(Long id);
}
