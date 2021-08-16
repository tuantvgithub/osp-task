package co.osp.base.businessservice.dto;

import lombok.Data;

@Data
public class NoteDTO {
    private Long id;
    private Long company_id;
    private Long business_type_id;
    private Long year;
    private Long quarter;
    private String type;
    private String note;

}
