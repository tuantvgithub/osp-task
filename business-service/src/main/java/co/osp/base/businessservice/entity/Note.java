package co.osp.base.businessservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Data
@Entity
@Table(name = "cpn_note")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Note {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_id")
    private Long company_id;

    @Column(name = "lic_business_type_id")
    private Long licBusinessTypeId;

    @Column(name = "year")
    private Long year;

    @Column(name = "quarter")
    private Long quarter;

    @Column(name = "type")
    private String type;

    @Column(name = "create_time")
    private String create_time;

    @Column(name = "update_time")
    private String update_time;

    @Size(max = 1000)
    @Column(name = "note", length = 500)
    private String note;

}
