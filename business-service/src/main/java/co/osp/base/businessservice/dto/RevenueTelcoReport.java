package co.osp.base.businessservice.dto;

import co.osp.base.businessservice.entity.Company;
import co.osp.base.businessservice.entity.File;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RevenueTelcoReport {
    private Company company;
    private List<RevenueTelcoServiceReport> serviceRevenueReports;
    private File file;

    public RevenueTelcoReport() {
        serviceRevenueReports = new ArrayList<RevenueTelcoServiceReport>();
        file = new File();
    }
}
