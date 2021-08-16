package co.osp.base.businessservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class SearchReceiptDTO {
    private List<Long> company_ids;
    private List<Long> service_ids;
    private List<Long> business_type_ids;
    private List<Long> payment_type_ids;
    private RangeDTO receipt_date;
    private RangeDTO amount_of_money;
    private String receipt_number;
    private String receipt_name;
    private List<String> receipt_numbers;
    private Long year;
    private Long quarter;
    private Long month;
    private String type;
    private Long full_paid;
    private Long index;
    private Long size;

    public SearchReceiptDTO() {
        this.receipt_date = new RangeDTO();
        this.amount_of_money = new RangeDTO();
    }

}
