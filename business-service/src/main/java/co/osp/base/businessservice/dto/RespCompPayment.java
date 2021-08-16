package co.osp.base.businessservice.dto;

import co.osp.base.businessservice.entity.Company;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RespCompPayment implements Comparable<RespCompPayment> {
    private Company company;
    private List<YearBusiness> years;

    public RespCompPayment() {
        this.years = new ArrayList<YearBusiness>();
    }

    @Override
    public int compareTo(RespCompPayment o) {
        return this.company.getName().compareTo(o.getCompany().getName());
    }
}
