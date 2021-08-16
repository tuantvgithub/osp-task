package co.osp.base.businessservice.dto;

import lombok.Data;

@Data
public class PaymentServiceDetail {
    private Long service_id;
    private Long business_type_id;
    private String business_type_name;
    private Long network_id;
    private String network_alias;
    private String service_name;
    private Long revenue;
    private Long need_pay;
    private Long paid_money;
    private Long owned_money;
    private String last_pay_date;
    private Long year;
    private Long quarter;
    private String type;

//    huytq
    private String receipt_number;
    private String note;
    private String receipt_name;
    private Long amount_of_money;
    private String receipt_date;
    private String receive_date;
    private String payer;
    private String receipt_content;
    private String receiver;
    private Long payment_type_id;
    private String file_id;
    private String file_name;
    private long company_id;

    public PaymentServiceDetail() {
        this.revenue = 0L;
        this.need_pay = 0L;
        this.paid_money = 0L;
        this.owned_money = 0L;
    }

}
