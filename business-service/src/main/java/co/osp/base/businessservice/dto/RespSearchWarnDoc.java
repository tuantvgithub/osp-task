package co.osp.base.businessservice.dto;

import co.osp.base.businessservice.entity.Company;
import co.osp.base.businessservice.entity.WarnDoc;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RespSearchWarnDoc {
    private Company company;
    private List<WarnDoc> warnDocs;

    public RespSearchWarnDoc() {
        this.warnDocs = new ArrayList<WarnDoc>();
    }

}
