package co.osp.base.businessservice.service.impl;

import co.osp.base.businessservice.entity.CpnCommonManager;
import co.osp.base.businessservice.repository.CpnCommonManagerRepository;
import co.osp.base.businessservice.service.CpnCommonManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CpnCommonManagerServiceImpl implements CpnCommonManagerService {

    private final CpnCommonManagerRepository ccmRepository;

    @Autowired
    public CpnCommonManagerServiceImpl(CpnCommonManagerRepository ccmRepository) {
        this.ccmRepository = ccmRepository;
    }

    @Override
    public CpnCommonManager search(Long businessTypeId, String type, Long year, Long quarter) {
        return ccmRepository.search(businessTypeId, type, year, quarter);
    }

    @Override
    public String getPayDeadline(Long businessTypeId, String type, Long year, Long quarter)
    {
        CpnCommonManager ccm = search(businessTypeId, type, year, quarter);
        if (ccm!=null) return ccm.getPayDeadline();
        return null;
    }

}
