package co.osp.base.businessservice.dto;

import co.osp.base.businessservice.entity.Receipt;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PaymentReceiptDTO {
    private Long company_id;
    private Long receipt_id;
    private Receipt receipt;
    private List<PaymentServiceDTO> paymentServices;

    public PaymentReceiptDTO() {
        this.paymentServices = new ArrayList<PaymentServiceDTO>();
    }

}
