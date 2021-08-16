package co.osp.base.businessservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A TlcCodeType.
 */
@Data
@Entity
@Table(name = "tlc_code_type")
public class TlcCodeType extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 32)
    @Column(name = "code", length = 32)
    private String code;

    @NotNull
    @Size(max = 128)
    @Column(name = "name", length = 128, nullable = false)
    private String name;

    @ManyToOne
    @JsonIgnoreProperties(value = "tlcCodeTypes", allowSetters = true)
    private KeyValue status;

    @Column(name = "is_root")
    private Boolean isRoot;

    @Column(name = "root_order")
    private String rootOrder;

    @ManyToOne
    @JsonIgnoreProperties(value = "tlcCodeTypes", allowSetters = true)
    private TlcCodeType parent;

    @ManyToOne
    @JsonIgnoreProperties(value = "tlcCodeTypes", allowSetters = true)
    private KeyValue structure;

    @Column(name = "total")
    private Long total;

    @Column(name = "fixed_ndc")
    private String fixedNdc;


    public TlcCodeType code(String code) {
        this.code = code;
        return this;
    }

    public TlcCodeType name(String name) {
        this.name = name;
        return this;
    }

    public TlcCodeType status(KeyValue status) {
        this.status = status;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TlcCodeType)) {
            return false;
        }
        return id != null && id.equals(((TlcCodeType) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "TlcCodeType{" +
            "id=" + id +
            ", code='" + code + '\'' +
            ", name='" + name + '\'' +
            ", status=" + status +
            ", parent=" + parent +
            ", structure=" + structure +
            ", total=" + total +
            '}';
    }
}
