package co.osp.base.businessservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class DebtDTO {
    private List<Long> company_ids;
    private String company_code;
    private String lic_number;
    private List<Long> business_type_ids;
    private List<Long> network_type_ids;
    private Long year;
    private Long quarter;
    private String type;
    private List<Long> years;
    private List<Long> quarters;
    private Long full_paid;
    private Long is_default;
    private Long is_paid_0101;
    private Long index;
    private Long size;

}
