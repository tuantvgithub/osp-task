package co.osp.base.businessservice.dto;

import co.osp.base.businessservice.entity.Note;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class QuarterBusiness {
    private Long quarter;
    private List<TelService> paymentServices;
    private Note quarter_note;
    private String in_doc_number;
    private String out_doc_number;

    public QuarterBusiness() {
        this.paymentServices = new ArrayList<TelService>();
    }

}
