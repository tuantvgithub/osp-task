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
 * A LicSrv.
 */
@Data
@Entity
@Table(name = "lic_srv")
public class LicSrv extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 128)
    @Column(name = "name", length = 128, nullable = false)
    private String name;

    @Size(max = 1024)
    @Column(name = "description", length = 1024)
    private String description;

    @Column(name = "order_no")
    private Integer orderNo;

    @ManyToOne
    @JsonIgnoreProperties(value = "children", allowSetters = true)
    private LicSrv parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = "parent", allowSetters = true)
    private Set<LicSrv> children = new HashSet<>();

    /**
     * Loại giấy phép
     */
    @ManyToMany
    @JoinTable(name = "lic_srv_lic_business_type",
        joinColumns = @JoinColumn(name = "lic_srv_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "lic_business_type_id", referencedColumnName = "id"))
    private Set<LicBusinessType> licBusinessTypes = new HashSet<>();


    public LicSrv name(String name) {
        this.name = name;
        return this;
    }

    public LicSrv description(String description) {
        this.description = description;
        return this;
    }

    public LicSrv orderNo(Integer orderNo) {
        this.orderNo = orderNo;
        return this;
    }

    public LicSrv parent(LicSrv licSrv) {
        this.parent = licSrv;
        return this;
    }

    public LicSrv children(Set<LicSrv> licSrvs) {
        this.children = licSrvs;
        return this;
    }

    public LicSrv addChildren(LicSrv licSrv) {
        this.children.add(licSrv);
        licSrv.setParent(this);
        return this;
    }

    public LicSrv removeChildren(LicSrv licSrv) {
        this.children.remove(licSrv);
        licSrv.setParent(null);
        return this;
    }

    public LicSrv addLicBusinessType(LicBusinessType licBusinessType) {
        this.licBusinessTypes.add(licBusinessType);
        return this;
    }

    public LicSrv removeLicBusinessType(LicBusinessType licBusinessType) {
        this.licBusinessTypes.remove(licBusinessType);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LicSrv)) {
            return false;
        }
        return id != null && id.equals(((LicSrv) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LicSrv{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", orderNo=" + getOrderNo() +
            "}";
    }
}
