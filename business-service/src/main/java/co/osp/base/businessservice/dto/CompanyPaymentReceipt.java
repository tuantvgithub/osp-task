package co.osp.base.businessservice.dto;

import co.osp.base.businessservice.entity.Company;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CompanyPaymentReceipt implements Comparable<CompanyPaymentReceipt> {
    private Company company;
    private List<PaymentReceitDetail> paymentReceitDetails;

    public CompanyPaymentReceipt() {
        this.paymentReceitDetails = new ArrayList<PaymentReceitDetail>();
    }

    @Override
    public int compareTo(CompanyPaymentReceipt o) {
        return this.company.getName().compareTo(o.getCompany().getName());
    }
}
