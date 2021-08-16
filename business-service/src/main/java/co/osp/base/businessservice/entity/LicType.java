package co.osp.base.businessservice.entity;


import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A LicType.
 */
@Data
@Entity
@Table(name = "lic_type")
public class LicType extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 128)
    @Column(name = "name", length = 128)
    private String name;

    @Size(max = 1024)
    @Column(name = "description", length = 1024)
    private String description;

    @Column(name = "order_no")
    private Integer orderNo;

    public LicType name(String name) {
        this.name = name;
        return this;
    }

    public LicType description(String description) {
        this.description = description;
        return this;
    }

    public LicType orderNo(Integer orderNo) {
        this.orderNo = orderNo;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LicType)) {
            return false;
        }
        return id != null && id.equals(((LicType) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LicType{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", orderNo=" + getOrderNo() +
            "}";
    }
}
