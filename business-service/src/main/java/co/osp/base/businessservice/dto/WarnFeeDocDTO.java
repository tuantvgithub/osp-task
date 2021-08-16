package co.osp.base.businessservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class WarnFeeDocDTO {
    private List<Long> company_ids;
    private Long times;
    private Long numberOfTime;
    private Long business_type_id;
    private Long year;
    private List<Long> years;
    private Long quarter;
    private String type;
    private WarnFeeDocAddition addition;

}
