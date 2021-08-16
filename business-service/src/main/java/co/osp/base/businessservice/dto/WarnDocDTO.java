package co.osp.base.businessservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class WarnDocDTO {
    private Long warn_doc_id;
    private Long company_id;
    private String doc_number;
    private String doc_type;
    private Long business_type_id;
    private List<Long> business_type_ids;
    private Long times;
    private Long numberOfTime;
    private Long file_id;
    private String doc_date;
    private List<Long> company_ids;
    private String warn_doc_number;
    private RangeDTO warn_doc_date;
    private Long year;
    private Long quarter;
    private String type;
    private String pay_deadline;
    private String note;

}
