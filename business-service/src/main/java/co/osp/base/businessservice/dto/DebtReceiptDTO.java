package co.osp.base.businessservice.dto;

import co.osp.base.businessservice.entity.Receipt;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DebtReceiptDTO {
    private Receipt receipt;
    private List<DebtServiceDTO> paymentServices;

    public DebtReceiptDTO() {
        this.paymentServices = new ArrayList<DebtServiceDTO>();
    }

}
