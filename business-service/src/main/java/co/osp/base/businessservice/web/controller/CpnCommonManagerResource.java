package co.osp.base.businessservice.web.controller;

import co.osp.base.businessservice.entity.CpnCommonManager;
import co.osp.base.businessservice.service.CpnCommonManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class CpnCommonManagerResource {

    private final CpnCommonManagerService cpnCommonManagerService;

    @Autowired
    public CpnCommonManagerResource(CpnCommonManagerService cpnCommonManagerService) {
        this.cpnCommonManagerService = cpnCommonManagerService;
    }

    @PostMapping("/revenue/cpnCommonManager/search")
    public ResponseEntity<CpnCommonManager> search(@RequestBody CpnCommonManager ccm)
    {
        CpnCommonManager cpnCommonManager = null;
        if (ccm.getType() != null && ccm.getType().equalsIgnoreCase("quarter")) {
            cpnCommonManager = this.cpnCommonManagerService
                                    .search(ccm.getBusinessTypeId(), ccm.getType(),
                                            ccm.getYear(), ccm.getQuarter());
        }else{
            cpnCommonManager = this.cpnCommonManagerService
                                    .search(ccm.getBusinessTypeId(), ccm.getType(),
                                            ccm.getYear(), null);
        }
        return ResponseEntity.ok(cpnCommonManager);
    }
}
