package co.osp.base.businessservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "telco_revenue")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RevenueTelco {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnoreProperties
    @JoinColumn(name = "lic_cpn_id", referencedColumnName = "id")
    private LicCpn licCpn;

    @Column(name = "lic_number")
    private String licNumber;

    @ManyToOne
    @JsonIgnoreProperties
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private Company company;

    @ManyToOne
    @JsonIgnoreProperties
    @JoinColumn(name = "file_id", referencedColumnName = "id")
    private File file;

    @Column(name = "quarter")
    private Long quarter;

    @Column(name = "year")
    private Long year;

    @Column(name = "revenue")
    private Long revenue;

    @Column(name = "type")
    private String type;

    @Column(name = "pay_deadline")
    private String payDeadline;

    @Column(name = "note")
    private String note;

}
