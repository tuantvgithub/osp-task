package co.osp.base.businessservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RespPaidServiceDTO {
    private Long network_id;
    private String network_alias;
    private Long need_pay;
    private Long paid_money;
    private Long owned_money;
}
