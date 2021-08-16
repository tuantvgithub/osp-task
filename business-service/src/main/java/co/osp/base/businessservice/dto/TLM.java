package co.osp.base.businessservice.dto;

import co.osp.base.businessservice.entity.Note;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TLM {
    private Long business_type_id;
    private String business_type_name;
    private List<TelService> paymentServices;
    private Note year_note;
    private String in_doc_number;
    private String out_doc_number;

    public TLM() {
        this.paymentServices = new ArrayList<TelService>();
    }

}
