package co.osp.base.businessservice.dto;

import co.osp.base.businessservice.entity.Company;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RespCompPaidFeeDTO {
    private Company company;
    private List<RespPaidFeeDTO> business_types;

    public RespCompPaidFeeDTO() {
        this.business_types = new ArrayList<RespPaidFeeDTO>();
    }

}
