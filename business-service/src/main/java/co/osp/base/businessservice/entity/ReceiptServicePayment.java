package co.osp.base.businessservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "receipt_service_payment")
public class ReceiptServicePayment {
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

    @Column(name = "paid_money")
    private Long paidMoney;

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

}
