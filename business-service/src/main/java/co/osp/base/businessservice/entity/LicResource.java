package co.osp.base.businessservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A LicResource.
 */
@Data
@Entity
@Table(name = "lic_resource")
public class LicResource extends AbstractAuditingEntity implements Serializable {

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

    @ManyToOne
    @JsonIgnoreProperties(value = "children", allowSetters = true)
    private LicResource parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = "parent", allowSetters = true)
    private Set<LicResource> children = new HashSet<>();

    /**
     * Loại giấy phép
     */
    @ManyToMany
    @JoinTable(name = "lic_resource_lic_business_type",
        joinColumns = @JoinColumn(name = "lic_resource_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "lic_business_type_id", referencedColumnName = "id"))
    private Set<LicBusinessType> licBusinessTypes = new HashSet<>();


    public LicResource name(String name) {
        this.name = name;
        return this;
    }

    public LicResource description(String description) {
        this.description = description;
        return this;
    }

    public LicResource orderNo(Integer orderNo) {
        this.orderNo = orderNo;
        return this;
    }

    public LicResource parent(LicResource licResource) {
        this.parent = licResource;
        return this;
    }

    public LicResource children(Set<LicResource> licResources) {
        this.children = licResources;
        return this;
    }

    public LicResource addChildren(LicResource licResource) {
        this.children.add(licResource);
        licResource.setParent(this);
        return this;
    }

    public LicResource removeChildren(LicResource licResource) {
        this.children.remove(licResource);
        licResource.setParent(null);
        return this;
    }

    public LicResource addLicBusinessType(LicBusinessType licBusinessType) {
        this.licBusinessTypes.add(licBusinessType);
        return this;
    }

    public LicResource removeLicBusinessType(LicBusinessType licBusinessType) {
        this.licBusinessTypes.remove(licBusinessType);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LicResource)) {
            return false;
        }
        return id != null && id.equals(((LicResource) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LicResource{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", orderNo=" + getOrderNo() +
            "}";
    }
}
