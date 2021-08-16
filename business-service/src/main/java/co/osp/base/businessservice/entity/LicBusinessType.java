package co.osp.base.businessservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A LicBusinessType.
 */
@Data
@Entity
@Table(name = "lic_business_type")
public class LicBusinessType extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 128)
    @Column(name = "name", length = 128, unique = true)
    private String name;
    @Size(max = 1024)
    @Column(name = "description", length = 1024)
    private String description;

    @Column(name = "order_no")
    private Integer orderNo;

    @ManyToOne
    @JsonIgnoreProperties(value = "children", allowSetters = true)
    private LicBusinessType parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = "parent", allowSetters = true)
    private Set<LicBusinessType> children = new HashSet<>();

    public LicBusinessType name(String name) {
        this.name = name;
        return this;
    }

    public LicBusinessType description(String description) {
        this.description = description;
        return this;
    }

    public LicBusinessType orderNo(Integer orderNo) {
        this.orderNo = orderNo;
        return this;
    }

    public LicBusinessType parent(LicBusinessType licBusinessType) {
        this.parent = licBusinessType;
        return this;
    }

    public LicBusinessType children(Set<LicBusinessType> licBusinessTypes) {
        this.children = licBusinessTypes;
        return this;
    }

    public LicBusinessType addChildren(LicBusinessType licBusinessType) {
        this.children.add(licBusinessType);
        licBusinessType.setParent(this);
        return this;
    }

    public LicBusinessType removeChildren(LicBusinessType licBusinessType) {
        this.children.remove(licBusinessType);
        licBusinessType.setParent(null);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LicBusinessType)) {
            return false;
        }
        return id != null && id.equals(((LicBusinessType) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LicBusinessType{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", orderNo=" + getOrderNo() +
            "}";
    }
}
