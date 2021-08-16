package co.osp.base.businessservice.dto;

import lombok.Data;

@Data
public class TelcoServiceDTO {
    private Long company_id;
    private Long business_type_id;
    private Long service_id;
    private Long network_id;
    private Long year;
    private Long quarter;
    private String type;
    private Long need_pay;

}
