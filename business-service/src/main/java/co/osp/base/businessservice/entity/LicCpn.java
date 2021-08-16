package co.osp.base.businessservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A LicCpn.
 */
@Data
@Entity
@Table(name = "lic_cpn")
public class LicCpn extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * Số lần cấp
     */
    @Column(name = "amount")
    private Integer amount;
    /**
     * Ngày tạo giấy phép
     */
    @NotNull
    @Column(name = "lic_created_date")
    private Instant licCreatedDate;
    /**
     * Thời gian thay đổi gần nhất
     */
    @Column(name = "change_date")
    private Instant changeDate;
    /**
     * Loại hình doanh nghiệp
     */
    @ManyToOne
    @JsonIgnoreProperties(value = "licCpns", allowSetters = true)
    private LicCpnOwner owner;
    /**
     * Vốn điều lệ
     */
    @Column(name = "auth_captial")
    private Long authCaptial;
    /**
     * Đơn vị cấp phép
     */
    @ManyToOne
    @JsonIgnoreProperties(value = "licCpns", allowSetters = true)
    private KeyValue licensee;
    /**
     * Số cấp phép
     */
    @NotNull
    @Size(max = 32)
    @Column(name = "lic_number", length = 32)
    private String licNumber;
    /**
     * Thời điểm chính thức cung cấp dịch vụ
     */
    @Column(name = "effective_date")
    private Instant effectiveDate;
    /**
     * Trạng thái: Vô hiệu, Đang hoạt động
     */
    @ManyToOne
    @JsonIgnoreProperties(value = "licCpns", allowSetters = true)
    private KeyValue status;
    /**
     * Ngày hết hạn
     */
    @Column(name = "expired_date")
    private Instant expiredDate;
    /**
     * Doanh nghiệp
     */
    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = "licCpns", allowSetters = true)
    private Company company;
    /**
     * File
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "lic_cpn_file",
        joinColumns = @JoinColumn(name = "lic_cpn_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "file_id", referencedColumnName = "id"))
    private Set<File> files = new HashSet<>();
    /**
     * Lãnh đạo xử lý
     */
    @Column(name = "leader_handle")
    private String leaderHandle;
    /**
     * Lãnh đạo ký
     */
    @Column(name = "leader_approve")
    private String leaderApprove;
    /**
     * Giấy phép: trường hợp cấp đổi, gia hạn, bổ sung, cấp lại
     */
    @ManyToOne
    @JsonIgnoreProperties(value = {"licCpns", "company", "file", "leaderHandle", "licCpn"}, allowSetters = true)
    private LicCpn licCpn;
    /**
     * Tỉnh
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "lic_cpn_province",
               joinColumns = @JoinColumn(name = "lic_cpn_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "province_id", referencedColumnName = "id"))
    private Set<Province> provinces = new HashSet<>();
    /**
     * Loại cấp phép
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "lic_cpn_lic_type",
               joinColumns = @JoinColumn(name = "lic_cpn_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "lic_type_id", referencedColumnName = "id"))
    private Set<LicType> licTypes = new HashSet<>();
    /**
     * Tài nguyên
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "lic_cpn_lic_resource",
               joinColumns = @JoinColumn(name = "lic_cpn_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "lic_resource_id", referencedColumnName = "id"))
    private Set<LicResource> licResources = new HashSet<>();
    /**
     * Loại giấy phép
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "lic_cpn_lic_business_type",
               joinColumns = @JoinColumn(name = "lic_cpn_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "lic_business_type_id", referencedColumnName = "id"))
    private Set<LicBusinessType> licBusinessTypes = new HashSet<>();
    /**
     * DỊch vụ
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "lic_cpn_lic_srv",
               joinColumns = @JoinColumn(name = "lic_cpn_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "lic_srv_id", referencedColumnName = "id"))
    private Set<LicSrv> licSrvs = new HashSet<>();
    /**
     * Loại mạng
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "lic_cpn_network_type",
        joinColumns = @JoinColumn(name = "lic_cpn_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "network_type_id", referencedColumnName = "id"))
    private Set<NetworkType> networkTypes = new HashSet<>();

    /**
     * Loại phạm vi
     */
    @ManyToOne
    @JsonIgnoreProperties(value = "licCpns", allowSetters = true)
    private KeyValue rangeType;

    /**
     * Giá trị phạm vi khác
     */
    @Size(max = 255)
    @Column(name = "other_range_value", length = 255)
    private String otherRangeValue;

    /**
     * Loại hình kinh tế doanh nghiệp
     */
    @ManyToOne
    private KeyValue companyEconomyType;

    /**
     * Địa chỉ doanh nghiệp
     */
    @Size(max = 255)
    @Column(name = "company_address", length = 255)
    private String companyAddress;

    /**
     * Tên doanh nghiệp
     */
    @Size(max = 255)
    @Column(name = "company_name", length = 255)
    private String companyName;

    /**
     * Cảnh báo lần một
     */
    @Column(name = "first_warning")
    private Boolean firstWarning;

    /**
     * Cảnh báo lần hai
     */
    @Column(name = "second_warning")
    private Boolean secondWarning;

    @Transient
    private Long restDate;

    @Size(max = 4000)
    @Column(name = "note", length = 4000)
    private String note;

    @Size(max = 256)
    @Column(name = "verifier", length = 256)
    private String verifier;

    /**
     * Real expired Time when status change to inactive
     */
    @Column(name = "expired_time")
    private Instant expiredTime;

    /**
     * Cảnh báo 45
     */
    @Column(name = "expired_45_warning")
    private Boolean expired45Warning;

    /**
     * Cảnh báo 90
     */
    @Column(name = "expired_90_warning")
    private Boolean expired90Warning;

    public LicCpn amount(Integer amount) {
        this.amount = amount;
        return this;
    }

    public LicCpn authCaptial(Long authCaptial) {
        this.authCaptial = authCaptial;
        return this;
    }

    public LicCpn effectiveDate(Instant effectiveDate) {
        this.effectiveDate = effectiveDate;
        return this;
    }

    public LicCpn expiredDate(Instant expiredDate) {
        this.expiredDate = expiredDate;
        return this;
    }

    public LicCpn company(Company company) {
        this.company = company;
        return this;
    }

    public LicCpn provinces(Set<Province> provinces) {
        this.provinces = provinces;
        return this;
    }

    public LicCpn licTypes(Set<LicType> licTypes) {
        this.licTypes = licTypes;
        return this;
    }

    public LicCpn addLicType(LicType licType) {
        this.licTypes.add(licType);
        return this;
    }

    public LicCpn removeLicType(LicType licType) {
        this.licTypes.remove(licType);
        return this;
    }

    public LicCpn licResources(Set<LicResource> licResources) {
        this.licResources = licResources;
        return this;
    }

    public LicCpn licBusinessTypes(Set<LicBusinessType> licBusinessTypes) {
        this.licBusinessTypes = licBusinessTypes;
        return this;
    }

    public LicCpn addLicBusinessType(LicBusinessType licBusinessType) {
        this.licBusinessTypes.add(licBusinessType);
        return this;
    }

    public LicCpn removeLicBusinessType(LicBusinessType licBusinessType) {
        this.licBusinessTypes.remove(licBusinessType);
        return this;
    }

    public LicCpn licSrvs(Set<LicSrv> licSrvs) {
        this.licSrvs = licSrvs;
        return this;
    }

    public LicCpn networkTypes(Set<NetworkType> networkTypes) {
        this.networkTypes = networkTypes;
        return this;
    }

    public LicCpn addFile(File file) {
        this.files.add(file);
        return this;
    }

    public LicCpn removeFile(File file) {
        this.files.remove(file);
        return this;
    }

    public Long getRestDate() {

        restDate = ChronoUnit.DAYS.between(Instant.now(), this.expiredDate);
        return restDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LicCpn)) return false;
        LicCpn licCpn1 = (LicCpn) o;
        return Objects.equals(getId(), licCpn1.getId()) &&
            Objects.equals(getAmount(), licCpn1.getAmount()) &&
            Objects.equals(getLicCreatedDate(), licCpn1.getLicCreatedDate()) &&
            Objects.equals(getChangeDate(), licCpn1.getChangeDate()) &&
            Objects.equals(getAuthCaptial(), licCpn1.getAuthCaptial()) &&
            Objects.equals(getLicensee(), licCpn1.getLicensee()) &&
            Objects.equals(getLicNumber(), licCpn1.getLicNumber()) &&
            Objects.equals(getEffectiveDate(), licCpn1.getEffectiveDate()) &&
            Objects.equals(getStatus(), licCpn1.getStatus()) &&
            Objects.equals(getExpiredDate(), licCpn1.getExpiredDate()) &&
            Objects.equals(getCompany(), licCpn1.getCompany()) &&
            Objects.equals(getFiles(), licCpn1.getFiles()) &&
            Objects.equals(getLeaderHandle(), licCpn1.getLeaderHandle()) &&
            Objects.equals(getLeaderApprove(), licCpn1.getLeaderApprove()) &&
            Objects.equals(getLicCpn(), licCpn1.getLicCpn()) &&
            Objects.equals(getProvinces(), licCpn1.getProvinces()) &&
            Objects.equals(getLicTypes(), licCpn1.getLicTypes()) &&
            Objects.equals(getLicResources(), licCpn1.getLicResources()) &&
            Objects.equals(getLicBusinessTypes(), licCpn1.getLicBusinessTypes()) &&
            Objects.equals(getLicSrvs(), licCpn1.getLicSrvs()) &&
            Objects.equals(getNetworkTypes(), licCpn1.getNetworkTypes()) &&
            Objects.equals(getRangeType(), licCpn1.getRangeType()) &&
            Objects.equals(getOtherRangeValue(), licCpn1.getOtherRangeValue()) &&
            Objects.equals(getCompanyEconomyType(), licCpn1.getCompanyEconomyType()) &&
            Objects.equals(getCompanyAddress(), licCpn1.getCompanyAddress()) &&
            Objects.equals(getCompanyName(), licCpn1.getCompanyName()) &&
            Objects.equals(getFirstWarning(), licCpn1.getFirstWarning()) &&
            Objects.equals(getSecondWarning(), licCpn1.getSecondWarning()) &&
            Objects.equals(getRestDate(), licCpn1.getRestDate()) &&
            Objects.equals(getNote(), licCpn1.getNote()) &&
            Objects.equals(getVerifier(), licCpn1.getVerifier());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getAmount(), getLicCreatedDate(), getChangeDate(), getOwner(), getAuthCaptial(), getLicensee(), getLicNumber(), getEffectiveDate(), getStatus(), getExpiredDate(), getCompany(), getFiles(), getLeaderHandle(), getLeaderApprove(), getLicCpn(), getProvinces(), getLicTypes(), getLicResources(), getLicBusinessTypes(), getLicSrvs(), getNetworkTypes(), getRangeType(), getOtherRangeValue(), getCompanyEconomyType(), getCompanyAddress(), getCompanyName(), getFirstWarning(), getSecondWarning(), getRestDate(), getNote(), getVerifier());
    }

}
