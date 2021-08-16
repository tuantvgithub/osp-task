package co.osp.base.businessservice.dto;

import co.osp.base.businessservice.entity.Company;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RespDebtDTO {
    private Company company;
    private List<DebtReceiptDTO> paymentReceiptDetails;

    public RespDebtDTO() {
        this.paymentReceiptDetails = new ArrayList<DebtReceiptDTO>();
    }

}
