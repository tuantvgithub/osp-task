package co.osp.base.businessservice.dto;

import lombok.Data;

@Data
public class PaymentServiceDTO {
    private Long business_type_id;
    private Long network_id;
    private Long service_id;
    private Long paid_money;
    private Long need_pay;
    private Long owned_pay;
    private Long quarter;
    private Long year;
    private String type;
    private String note;

}
