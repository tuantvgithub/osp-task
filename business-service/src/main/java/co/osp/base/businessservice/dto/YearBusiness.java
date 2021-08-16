package co.osp.base.businessservice.dto;

import lombok.Data;

@Data
public class YearBusiness {

    private Long year;
    private CCDVVT ccdvvt;
    private TLM tlm;
    private YearSummary year_summary;

}
