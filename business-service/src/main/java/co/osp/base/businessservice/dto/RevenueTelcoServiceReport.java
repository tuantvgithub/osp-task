package co.osp.base.businessservice.dto;

import lombok.Data;

@Data
public class RevenueTelcoServiceReport {
    private String serviceType;
    private String licNumber;
    private Long year;
    private Long quarter;
    private Long revenue;

}
