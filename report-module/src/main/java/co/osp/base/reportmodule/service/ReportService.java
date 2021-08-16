package co.osp.base.reportmodule.service;

import co.osp.base.reportmodule.dto.TestDto;
import net.sf.jasperreports.engine.JRException;

import java.io.FileNotFoundException;
import java.util.List;

public interface ReportService {

    String exportTest(String reportTemplateFileName, String destFileName, String format,
                      List<TestDto> testDtoList) throws FileNotFoundException, JRException;
}
