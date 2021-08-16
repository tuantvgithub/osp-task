package co.osp.base.businessservice.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Data
@Entity
@Table(name = "lic_cpn_owner", uniqueConstraints = { @UniqueConstraint(columnNames = "name", name = "uniqueNameConstraint")})
public class LicCpnOwner implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "name", unique = true)
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LicCpnOwner)) return false;
        LicCpnOwner that = (LicCpnOwner) o;
        return Objects.equals(getId(), that.getId()) &&
            Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }

    @Override
    public String toString() {
        return "LicCpnOwner{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
    }
}
