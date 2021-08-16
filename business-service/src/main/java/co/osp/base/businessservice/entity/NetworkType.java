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
 * A NetworkType.
 */
@Data
@Entity
@Table(name = "network_type")
public class NetworkType extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 256)
    @Column(name = "name", length = 256, nullable = false)
    private String name;

    @Size(max = 256)
    @Column(name = "description", length = 256)
    private String description;

    @Column(name = "order_no")
    private Integer orderNo;

    @ManyToOne
    @JsonIgnoreProperties(value = "children", allowSetters = true)
    private NetworkType parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = "parent", allowSetters = true)
    private Set<NetworkType> children = new HashSet<>();

    /**
     * Loại giấy phép
     */
    @ManyToMany
    @JoinTable(name = "network_type_lic_business_type",
        joinColumns = @JoinColumn(name = "network_type_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "lic_business_type_id", referencedColumnName = "id"))
    private Set<LicBusinessType> licBusinessTypes = new HashSet<>();

    @Column(name = "alias")
    private String alias;

    public NetworkType name(String name) {
        this.name = name;
        return this;
    }

    public NetworkType description(String description) {
        this.description = description;
        return this;
    }

    public NetworkType orderNo(Integer orderNo) {
        this.orderNo = orderNo;
        return this;
    }

    public NetworkType parent(NetworkType networkType) {
        this.parent = networkType;
        return this;
    }

    public NetworkType children(Set<NetworkType> networkTypes) {
        this.children = networkTypes;
        return this;
    }

    public NetworkType addChildren(NetworkType networkType) {
        this.children.add(networkType);
        networkType.setParent(this);
        return this;
    }

    public NetworkType removeChildren(NetworkType networkType) {
        this.children.remove(networkType);
        networkType.setParent(null);
        return this;
    }

    public NetworkType addLicBusinessType(LicBusinessType licBusinessType) {
        this.licBusinessTypes.add(licBusinessType);
        return this;
    }

    public NetworkType removeLicBusinessType(LicBusinessType licBusinessType) {
        this.licBusinessTypes.remove(licBusinessType);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NetworkType)) {
            return false;
        }
        return id != null && id.equals(((NetworkType) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NetworkType{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", orderNo=" + getOrderNo() +
            "}";
    }

}
