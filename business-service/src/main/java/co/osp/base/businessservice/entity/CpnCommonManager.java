package co.osp.base.businessservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "cpn_common_manager")
public class CpnCommonManager implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "create_time")
    private String createTime;

    @Column(name = "update_time")
    private String updateTime;

    @Column(name = "business_type_id")
    private Long businessTypeId;

    @Column(name = "network_type_id")
    private Long networkTypeId;

    @Column(name = "type")
    private String type;

    @Column(name = "year")
    private Long year;

    @Column(name = "quarter")
    private Long quarter;

    @Column(name = "pay_deadline")
    private String payDeadline;
}
