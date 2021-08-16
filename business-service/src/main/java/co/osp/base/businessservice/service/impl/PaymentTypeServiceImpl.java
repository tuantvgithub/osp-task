package co.osp.base.businessservice.service.impl;

import co.osp.base.businessservice.entity.PaymentType;
import co.osp.base.businessservice.repository.PaymentTypeRepository;
import co.osp.base.businessservice.service.PaymentTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PaymentTypeServiceImpl implements PaymentTypeService {

    private final PaymentTypeRepository paymentTypeRepository;

    @Autowired
    public PaymentTypeServiceImpl(PaymentTypeRepository paymentTypeRepository) {
        this.paymentTypeRepository = paymentTypeRepository;
    }

    @Override
    public List<PaymentType> search()
    {
        return this.paymentTypeRepository.findAll();
    }

    @Override
    public List<PaymentType> findByListIds(List<Long> ids) {
        return this.paymentTypeRepository.findByListIds(ids);
    }

    @Override
    public PaymentType findById(Long id) {
        Optional<PaymentType> paymentType = this.paymentTypeRepository.findById(id);
        return paymentType.orElse(null);
    }
}
