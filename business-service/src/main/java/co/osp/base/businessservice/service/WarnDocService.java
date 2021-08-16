package co.osp.base.businessservice.service;

import co.osp.base.businessservice.entity.WarnDoc;
import co.osp.base.businessservice.dto.RespSearchWarnDoc;
import co.osp.base.businessservice.dto.WarnDocDTO;

import java.util.List;

public interface WarnDocService {
    public List<RespSearchWarnDoc> search(WarnDocDTO warnDocDTO);
    public void del(Long warnDocId);
    public void update(WarnDocDTO warnDoc) throws Exception;
    public WarnDoc create(WarnDocDTO warnDoc) throws Exception;
}
