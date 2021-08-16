package co.osp.base.businessservice.service;

import co.osp.base.businessservice.entity.Company;
import co.osp.base.businessservice.entity.File;
import co.osp.base.businessservice.dto.WarnFeeDocDTO;
import co.osp.base.businessservice.dto.RevenueTelcoReport;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.List;

public interface RevenueTelcoService {

    XSSFWorkbook generateReport(List<Long> companyIds, Long year, Long quarter, String type);
    List<RevenueTelcoReport> search(List<Long> companyIds, Long year, Long quarter, String type);
    void del(List<Company> companyIds, Long year, Long quater, String type);
    void delFile(Long fileId);
    void upload(Long year, Long quarter, File file, String type) throws Exception;
    String generateWarnFeeDocs(WarnFeeDocDTO warnFeeDocDTO) throws IOException;
    String generateKhoSoWarnFeeDocs(WarnFeeDocDTO wfdDTO) throws IOException;

}
