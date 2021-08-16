package co.osp.base.reportmodule.service.impl;

import co.osp.base.reportmodule.dto.TestDto;
import co.osp.base.reportmodule.service.ReportService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

@Service
public class JasperReportServiceImpl implements ReportService {

    @Value("${app.reportTemplatePath}")
    private String reportTemplatePath;

    @Value("${app.destFilePath}")
    private String destFilePath;

    @Override
    public String exportTest(String reportTemplateFileName, String destFileName,
                             String format, List<TestDto> testDtoList
    ) throws FileNotFoundException, JRException {

        File file = ResourceUtils.getFile(this.reportTemplatePath + reportTemplateFileName);
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(testDtoList);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("COMPANY_NAME", "TUANTV");
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);

        File destDirectory = new File(this.destFilePath);
        destDirectory.mkdirs();

        if (format.equalsIgnoreCase("pdf")) {
            JasperExportManager.exportReportToPdfFile(jasperPrint,
                            this.destFilePath + destFileName);
        } else if (format.equalsIgnoreCase("html")) {
            JasperExportManager.exportReportToHtmlFile(jasperPrint,
                            this.destFilePath + destFileName);
        }

        return this.destFilePath + destFileName;
    }

}
