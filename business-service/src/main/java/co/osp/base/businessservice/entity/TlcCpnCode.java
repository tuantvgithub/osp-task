package co.osp.base.businessservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A TlcCpnCode.
 */
@Data
@Entity
@Table(name = "tlc_cpn_code")
public class TlcCpnCode extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnoreProperties(value = "tlcCodes", allowSetters = true)
    private KeyValue pattern;

    @NotNull
    @Size(max = 32)
    @Column(name = "code", length = 32, nullable = false)
    private String code;

    @NotNull
    @Column(name = "length", nullable = false)
    private Integer length;

    @Column(name = "amount")
    private Long amount;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = "tlcCodes", allowSetters = true)
    private TlcCodeType tlcCodeType;

    @Size(max = 128)
    @Column(name="ndc_code", length = 128)
    private String ndcCode;

    @ManyToOne
    @JsonIgnoreProperties(value = "tlcCodes", allowSetters = true)
    private Province province;

    @Column(name = "province_name")
    private String provinceName;
    /**
     * Ngày quyết định
     */
    @NotNull
    @Column(name = "decision_date", nullable = false)
    private Instant decisionDate;
    /**
     * Ngày hiệu lực
     */
    @NotNull
    @Column(name = "effective_date", nullable = false)
    private Instant effectiveDate;
    /**
     * Kiểu hiện trạng
     */
    @Column(name = "type")
    private String type;
    /**
     * Ngày hết hạn
     */
    @Column(name = "expire_date")
    private Instant expireDate;

    /**
     * Doanh nghiệp
     */
    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = "tlcCpnCodes", allowSetters = true)
    private Company company;

    /**
     * Mã số, khối số
     */
    @ManyToMany
    @JoinTable(name = "tlc_cpn_code_tlc_code",
        joinColumns = @JoinColumn(name = "tlc_cpn_code_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "tlc_code_id", referencedColumnName = "id"))
    private Set<TlcCode> tlcCodeHis = new HashSet<>();

    @Size(max = 128)
    @Column(name = "start", length = 128)
    private String start;

    @Size(max = 128)
    @Column(name = "end", length = 128)
    private String end;

    @ManyToOne()
    @JsonIgnoreProperties(value = "tlcCodes", allowSetters = true)
    private TlcCodeFee tlcCodeFee;


    public TlcCpnCode amount(Long amount) {
        this.amount = amount;
        return this;
    }

    public TlcCpnCode type(String type) {
        this.type = type;
        return this;
    }

    public TlcCpnCode expireDate(Instant expireDate) {
        this.expireDate = expireDate;
        return this;
    }

    public TlcCpnCode company(Company company) {
        this.company = company;
        return this;
    }

    public TlcCpnCode addTlcCodeHis(TlcCode tlcCode) {
        this.tlcCodeHis.add(tlcCode);
        return this;
    }

    public TlcCpnCode removeTlcCodeHis(TlcCode tlcCode) {
        this.tlcCodeHis.remove(tlcCode);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TlcCpnCode)) {
            return false;
        }
        return id != null && id.equals(((TlcCpnCode) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

}
