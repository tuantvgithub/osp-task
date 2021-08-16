package co.osp.base.businessservice.service;

import co.osp.base.businessservice.entity.CpnCommonManager;

public interface CpnCommonManagerService {

    CpnCommonManager search(Long businessTypeId, String type, Long year, Long quarter);
    String getPayDeadline(Long businessTypeId, String type, Long year, Long quarter);

}
