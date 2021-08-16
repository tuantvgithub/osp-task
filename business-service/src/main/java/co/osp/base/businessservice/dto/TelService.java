package co.osp.base.businessservice.dto;

import lombok.Data;

@Data
public class TelService {
    private Long network_id;
    private String network_alias;
    private Long revenue;
    private Long need_pay_revenue;
    private Long need_pay;
    private Long need_pay_0101;
    private Long paid_money;
    private Long paid_money_quarters;
    private Long paid_money_year;
    private Long owned_money;
    private String pay_deadline;
    private Long year;
    private Long quarter;
    private String type;
    private String lic_number;
    private String lic_id;
    private String lic_start_date;
    private String lic_end_date;

    public TelService() {
        this.revenue = null;
        this.need_pay = null;
        this.paid_money = 0L;
        this.owned_money = null;
        this.need_pay_0101 = 0L;
        this.paid_money_quarters = 0L;
        this.paid_money_year = 0L;
        this.need_pay_revenue = 0L;
        this.lic_number = "";
        this.lic_id = "";
    }

}
