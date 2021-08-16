package co.osp.base.businessservice.dto;

import co.osp.base.businessservice.entity.Receipt;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PaymentReceitDetail implements Comparable<PaymentReceitDetail> {
    private Receipt receipt;
    private List<PaymentServiceDetail> paymentServices;
    private String user;
    private String createDate;

    public PaymentReceitDetail() {
        this.paymentServices = new ArrayList<PaymentServiceDetail>();
    }

    @Override
    public int compareTo(PaymentReceitDetail paymentReceitDetail) {
        return (int)(this.createDate.compareTo(paymentReceitDetail.createDate));
    }
}
