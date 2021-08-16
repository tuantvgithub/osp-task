package co.osp.base.businessservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

/**
 * A Company.
 */
@Data
@Entity
@Table(name = "company", uniqueConstraints = { @UniqueConstraint(columnNames = "code", name = "uniqueCompanyNameConstraint")})
public class Company extends AbstractAuditingEntity implements Serializable, Comparator<Company> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 128)
    @Column(name = "code", length = 128,  unique = true)
    private String code;

    @NotNull
    @Size(max = 128)
    @Column(name = "name", length = 128, nullable = false, unique = true)
    private String name;

    @Size(max = 128)
    @Column(name = "short_name", length = 128, unique = true)
    private String shortName;

    @Size(max = 256)
    @Column(name = "address", length = 256)
    private String address;

    @Size(max = 256)
    @Column(name = "address_contact", length = 256)
    private String addressContact;

    @Size(max = 256)
    @Column(name = "international_name", length = 256)
    private String internationalName;

    @Size(max = 256)
    @Column(name = "representation_person", length = 256)
    private String representationPerson;

    @Size(max = 256)
    @Column(name = "email", length = 256)
    private String email;

    @Column(name = "phone")
    private String phone;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = "companies", allowSetters = true)
    private CompanyType companyType;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = "companies", allowSetters = true)
    private Province province;

    @Column(name = "website")
    private String website;

    @Column(name = "contact_person")
    private String contactPerson;

    public Company code(String code) {
        this.code = code;
        return this;
    }

    public Company name(String name) {
        this.name = name;
        return this;
    }

    public Company shortName(String shortName) {
        this.shortName = shortName;
        return this;
    }

    public Company address(String address) {
        this.address = address;
        return this;
    }

    public Company internationalName(String internationalName) {
        this.internationalName = internationalName;
        return this;
    }

    public Company representationPerson(String representationPerson) {
        this.representationPerson = representationPerson;
        return this;
    }

    public Company email(String email) {
        this.email = email;
        return this;
    }

    public Company phone(String phone) {
        this.phone = phone;
        return this;
    }

    public Company companyType(CompanyType companyType) {
        this.companyType = companyType;
        return this;
    }

    public Company province(Province province) {
        this.province = province;
        return this;
    }

    @Override
    public int compare(Company o1, Company o2) {
        return o1.getName().trim().compareToIgnoreCase(o2.getName().trim());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Company)) return false;
        Company company = (Company) o;
        return Objects.equals(getId(), company.getId()) &&
            Objects.equals(getCode(), company.getCode()) &&
            Objects.equals(getName(), company.getName()) &&
            Objects.equals(getShortName(), company.getShortName()) &&
            Objects.equals(getAddress(), company.getAddress()) &&
            Objects.equals(getAddressContact(), company.getAddressContact()) &&
            Objects.equals(getInternationalName(), company.getInternationalName()) &&
            Objects.equals(getRepresentationPerson(), company.getRepresentationPerson()) &&
            Objects.equals(getEmail(), company.getEmail()) &&
            Objects.equals(getPhone(), company.getPhone()) &&
            Objects.equals(getCompanyType(), company.getCompanyType()) &&
            Objects.equals(getProvince(), company.getProvince()) &&
            Objects.equals(getWebsite(), company.getWebsite()) &&
            Objects.equals(getContactPerson(), company.getContactPerson());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getId(), getCode(), getName(),
                getShortName(), getAddress(), getAddressContact(),
                getInternationalName(), getRepresentationPerson(),
                getEmail(), getPhone(), getCompanyType(),
                getProvince(), getWebsite(), getContactPerson());
    }

    @Override
    public String toString() {
        return "Company{" +
            "id=" + id +
            ", code='" + code + '\'' +
            ", name='" + name + '\'' +
            ", shortName='" + shortName + '\'' +
            ", address='" + address + '\'' +
            ", addressContact='" + addressContact + '\'' +
            ", internationalName='" + internationalName + '\'' +
            ", representationPerson='" + representationPerson + '\'' +
            ", email='" + email + '\'' +
            ", phone='" + phone + '\'' +
            ", companyType=" + companyType +
            ", province=" + province +
            ", website='" + website + '\'' +
            ", contactPerson='" + contactPerson + '\'' +
            '}';
    }
}
