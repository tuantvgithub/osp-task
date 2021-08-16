package co.osp.base.businessservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "receipt_service_payment_his")
public class ReceiptServicePaymentHis {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnoreProperties
    @JoinColumn(name = "receipt_id", referencedColumnName = "id")
    private Receipt receipt;

    @ManyToOne
    @JsonIgnoreProperties
    @JoinColumn(name = "lic_cpn_id", referencedColumnName = "id")
    private LicCpn licCpn;

    @ManyToOne
    @JsonIgnoreProperties
    @JoinColumn(name = "network_id", referencedColumnName = "id")
    private NetworkType networkType;

    @ManyToOne
    @JsonIgnoreProperties
    @JoinColumn(name = "lic_business_type_id", referencedColumnName = "id")
    private LicBusinessType licBusinessType;

    @Column(name = "need_pay")
    private Long need_pay;

    @Column(name = "paid_money")
    private Long paidMoney;

    @Column(name = "owned_money")
    private Long owned_money;

    @Column(name = "create_time")
    private String createTime;

    @Column(name = "update_time")
    private String updateTime;

    @Column(name = "quarter")
    private Long quarter;

    @Column(name = "year")
    private Long year;

    @Column(name = "type")
    private String type;

    @Column(name = "state")
    private String state;

    @Column(name = "note")
    private String note;

    @Column(name = "company_id")
    private Long compId;

    @Column(name = "user")
    private String user;

    @Column(name = "action")
    private String action;

    //// HUyTQ
    @Column(name = "receipt_number")
    private String receiptNumber;

    @Column(name = "receipt_name")
    private String receiptName;

    @Column(name = "amount_of_money")
    private Long amountOfMoney;

    @Column(name = "receipt_date")
    private String receiptDate;

    @Column(name = "receive_date")
    private String receive_date;

    @Column(name = "receipt_content")
    private String receiptContent;

    @Column(name = "payer")
    private String payer;

    @Column(name = "receiver")
    private String receiver;

    @Column(name = "payment_type_id")
    private Long payment_type_ids ;

    @Column(name = "file_id")
    private String file_id;

    @Column(name = "file_name")
    private String file_name;

}
