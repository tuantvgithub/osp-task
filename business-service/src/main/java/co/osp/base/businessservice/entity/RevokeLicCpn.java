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
 * A RevokeLicCpn.
 */
@Data
@Entity
@Table(name = "revoke_lic_cpn")
public class RevokeLicCpn extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 128)
    @Column(name = "revoke_number", length = 128)
    private String revokeNumber;

    @Column(name = "issued_date")
    private Instant issuedDate;

    /**
     * File
     */
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(name = "revoke_lic_cpn_file",
        joinColumns = @JoinColumn(name = "revoke_lic_cpn_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "file_id", referencedColumnName = "id"))
    private Set<File> files = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = "revokeLicCpns", allowSetters = true)
    private LicCpn licCpn;


    public RevokeLicCpn issuedDate(Instant issuedDate) {
        this.issuedDate = issuedDate;
        return this;
    }

    public RevokeLicCpn addFile(File file) {
        this.files.add(file);
        return this;
    }

    public RevokeLicCpn removeFile(File file) {
        this.files.remove(file);
        return this;
    }


    public RevokeLicCpn licCpn(LicCpn licCpn) {
        this.licCpn = licCpn;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RevokeLicCpn)) {
            return false;
        }
        return id != null && id.equals(((RevokeLicCpn) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "RevokeLicCpn{" +
            "id=" + id +
            ", revokeNumber='" + revokeNumber + '\'' +
            ", issuedDate=" + issuedDate +
            '}';
    }


}
