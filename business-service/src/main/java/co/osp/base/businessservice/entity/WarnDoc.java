package co.osp.base.businessservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "warn_doc")
public class WarnDoc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnoreProperties
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private Company company;

    @Column(name = "doc_number")
    private String doc_number;

    @Column(name = "doc_type")
    private String doc_type;

    @ManyToOne
    @JsonIgnoreProperties
    @JoinColumn(name = "business_type_id", referencedColumnName = "id")
    private LicBusinessType licBusinessType;

    @Column(name = "doc_date")
    private String doc_date;

    @Column(name = "create_time")
    private String create_time;

    @Column(name = "update_time")
    private String update_time;

    @Column(name = "times")
    private Long times;

    @ManyToOne
    @JsonIgnoreProperties
    @JoinColumn(name = "file_id", referencedColumnName = "id")
    private File file;

    @Column(name = "year")
    private Long year;

    @Column(name = "quarter")
    private Long quarter;

    @Column(name = "type")
    private String type;

    @Column(name = "pay_deadline")
    private String payDeadline;

    @Column(name = "number_of_time")
    private Long numberOfTime;

    @Column(name = "note")
    private String note;

}
