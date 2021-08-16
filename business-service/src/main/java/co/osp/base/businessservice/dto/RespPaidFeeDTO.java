package co.osp.base.businessservice.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RespPaidFeeDTO {
    private Long feeType;
    private Long business_type_id;
    private String business_type_name;
    private List<RespPaidServiceDTO> networks;

    public RespPaidFeeDTO() {
        this.networks = new ArrayList<RespPaidServiceDTO>();
    }

}
