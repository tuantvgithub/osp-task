package co.osp.base.businessservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A TlcCodeFee.
 */
@Data
@Entity
@Table(name = "tlc_code_fee")
public class TlcCodeFee extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 256)
    @Column(name = "description", length = 256)
    private String description;

    @Column(name = "tt")
    private String tt;

    @NotNull
    @Size(max = 256)
    @Column(name = "name", length = 256, nullable = false)
    private String name;

    @NotNull
    @Size(max = 256)
    @Column(name = "fee", length = 256, nullable = false)
    private String fee;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = "tlcCodeFees", allowSetters = true)
    private TlcCodeType tlcCodeType;

    @NotNull
    @Column(name = "order_no", nullable = false)
    private String orderNo;

    @NotNull
    @Column(name = "level", nullable = false)
    private String level;

    public TlcCodeFee description(String description) {
        this.description = description;
        return this;
    }

    public TlcCodeFee tt(String tt) {
        this.tt = tt;
        return this;
    }

    public TlcCodeFee name(String name) {
        this.name = name;
        return this;
    }

    public TlcCodeFee fee(String fee) {
        this.fee = fee;
        return this;
    }

    public TlcCodeFee tlcCodeType(TlcCodeType tlcCodeType) {
        this.tlcCodeType = tlcCodeType;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TlcCodeFee)) {
            return false;
        }
        return id != null && id.equals(((TlcCodeFee) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TlcCodeFee{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", tt='" + getTt() + "'" +
            ", name='" + getName() + "'" +
            ", fee='" + getFee() + "'" +
            "}";
    }
}
