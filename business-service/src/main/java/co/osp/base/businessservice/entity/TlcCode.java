package co.osp.base.businessservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A TlcCode.
 */
@Data
@Entity
@Table(name = "tlc_code")
public class TlcCode extends AbstractAuditingEntity implements Serializable {

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

    @Column(name = "tlc_code_type_id", insertable=false, updatable=false)
    private Long tlcCodeTypeId;

    @Size(max = 128)
    @Column(name="ndc_code", length = 128)
    private String ndcCode;

    @ManyToOne
    @JsonIgnoreProperties(value = "tlcCodes", allowSetters = true)
    private Province province;

    @Column(name = "province_id",insertable=false, updatable=false)
    private Long provinceId;

    @ManyToOne
    @JsonIgnoreProperties(value = "tlcCodes", allowSetters = true)
    private DecisionHis decisionHis;

    @Size(max = 128)
    @Column(name = "start", length = 128)
    private String start;

    @Size(max = 128)
    @Column(name = "end", length = 128)
    private String end;

    @ManyToOne()
    @JsonIgnoreProperties(value = "tlcCodes", allowSetters = true)
    private TlcCodeFee tlcCodeFee;

    @ManyToMany
    @JoinTable(name = "tlc_cpn_code_tlc_code",
        joinColumns = @JoinColumn(name = "tlc_code_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "tlc_cpn_code_id", referencedColumnName = "id"))
    private Set<TlcCpnCode> tlcCpnCodes = new HashSet<>();

    public TlcCode code(String code) {
        this.code = code;
        return this;
    }

    public TlcCode length(Integer length) {
        this.length = length;
        return this;
    }

    public TlcCode amount(Long amount) {
        this.amount = amount;
        return this;
    }

    public TlcCode tlcCodeType(TlcCodeType tlcCodeType) {
        this.tlcCodeType = tlcCodeType;
        return this;
    }

    public TlcCode province(Province province) {
        this.province = province;
        return this;
    }

    public TlcCode decisionHis(DecisionHis decisionHis) {
        this.decisionHis = decisionHis;
        return this;
    }

    public TlcCode addTlcCpnCode(TlcCpnCode tlcCpnCode) {
        this.tlcCpnCodes.add(tlcCpnCode);
        return this;
    }

    public TlcCode removeTlcCpnCode(TlcCpnCode tlcCpnCode) {
        this.tlcCpnCodes.remove(tlcCpnCode);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TlcCode)) {
            return false;
        }
        return id != null && id.equals(((TlcCode) o).id);
    }


    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "TlcCode{" +
            "id=" + id +
            ", pattern=" + pattern +
            ", code='" + code + '\'' +
            ", length=" + length +
            ", amount=" + amount +
//            ", status='" + status + '\'' +
            ", tlcCodeType=" + tlcCodeType +
            ", ndcCode=" + ndcCode +
            ", province=" + province +
            ", decisionHis=" + decisionHis +
            ", start='" + start + '\'' +
            ", end='" + end + '\'' +
            ", tlcCodeFee=" + tlcCodeFee +
            '}';
    }
}
