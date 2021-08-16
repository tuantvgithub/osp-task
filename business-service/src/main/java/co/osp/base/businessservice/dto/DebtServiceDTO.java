package co.osp.base.businessservice.dto;

import lombok.Data;

@Data
public class DebtServiceDTO {
    private Long business_type_id;
    private String business_type_name;
    private Long network_id;
    private String network_alias;
    private Long revenue;
    private Long need_pay;
    private Long paid_money;
    private Long owned_money;
    private Long year;
    private Long quarter;
    private String type;

}
