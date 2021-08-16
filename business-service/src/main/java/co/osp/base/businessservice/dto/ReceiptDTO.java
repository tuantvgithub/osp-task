package co.osp.base.businessservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReceiptDTO {
    private Long receipt_id;
    private Long company_id;
    private String receipt_number;
    private String receipt_name;
    private String receipt_date;
    private String receive_date;
    private String payer;
    private String receiver;
    private Long amount_of_money;
    private Long payment_type_id;
    private Long file_id;
    private List<Long> file_ids;
    private String note;

    public ReceiptDTO() {
        this.file_ids = new ArrayList<Long>();
    }

}
