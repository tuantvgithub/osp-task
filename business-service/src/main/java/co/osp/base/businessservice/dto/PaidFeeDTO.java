package co.osp.base.businessservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class PaidFeeDTO {
    private List<Long> company_ids;
    private Long business_type_id;
    private Long year;
    private Long quarter;
    private String type;

}
