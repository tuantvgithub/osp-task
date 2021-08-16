package co.osp.base.businessservice.dto;

import lombok.Data;

@Data
public class YearSummary {
    private Long revenue;
    private Long need_pay_revenue;
    private Long need_pay;
    private Long need_pay_0101;
    private Long paid_money;
    private Long paid_money_quarters;
    private Long paid_money_year;
    private Long owned_money;

    public YearSummary() {
        this.revenue = 0L;
        this.need_pay_revenue = 0L;
        this.need_pay = 0L;
        this.need_pay_0101 = 0L;
        this.paid_money = 0L;
        this.paid_money_quarters = 0L;
        this.paid_money_year = 0L;
        this.owned_money = 0L;
    }

}
