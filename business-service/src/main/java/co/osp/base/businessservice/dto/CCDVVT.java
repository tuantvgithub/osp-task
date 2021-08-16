package co.osp.base.businessservice.dto;

import co.osp.base.businessservice.entity.Note;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CCDVVT {

    private Long business_type_id;
    private String business_type_name;
    private List<QuarterBusiness> quarters;
    private QuarterBusiness year;
    private Note year_note;

    public CCDVVT() {
        this.quarters = new ArrayList<QuarterBusiness>();
    }

}
